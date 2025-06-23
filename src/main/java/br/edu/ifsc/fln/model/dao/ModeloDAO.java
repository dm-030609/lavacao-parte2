package br.edu.ifsc.fln.model.dao;


import br.edu.ifsc.fln.model.domain.ETipoCombustivel;
import br.edu.ifsc.fln.model.domain.Marca;
import br.edu.ifsc.fln.model.domain.Modelo;
import br.edu.ifsc.fln.model.domain.Motor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModeloDAO {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(Modelo modelo, Motor motor) {
        String sql = "INSERT INTO modelo(descricao, id_marca) VALUES(?,?);";
        String sqlMotor = "INSERT INTO motor(id_modelo, potencia, tipoCombustivel) VALUES (?,?,?);";
        try {
            // Inserindo o modelo e capturando o ID gerado
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, modelo.getDescricao());
            stmt.setInt(2, modelo.getMarca().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) {
                int id_modelo = rs.getInt(1);
                modelo.setId(id_modelo);
            }

            stmt = connection.prepareStatement(sqlMotor);
            stmt.setInt(1, modelo.getId());
            stmt.setInt(2, motor.getPotencia());
            stmt.setString(3, motor.geteTipoCombustivel().name());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean alterar(Modelo modelo) {
        String sql = "UPDATE modelo SET descricao=?, categoria=?, id_marca=? WHERE id=?";
        String sqlMotor = "UPDATE motor set potencia=?, tipoCombustivel=? WHERE id_modelo=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, modelo.getDescricao());
            stmt.setString(2, modelo.geteCategoria().name());
            stmt.setInt(3, modelo.getMarca().getId());
            stmt.setInt(4, modelo.getId());
            stmt.execute();

            PreparedStatement stmtMotor = connection.prepareStatement(sqlMotor);
            stmtMotor.setInt(1, modelo.getMotor().getPotencia());
            stmtMotor.setString(2, modelo.getMotor().geteTipoCombustivel().name());
            stmtMotor.setInt(3, modelo.getId());
            stmtMotor.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean remover(Modelo modelo) {
        String sql = "DELETE FROM modelo WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, modelo.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Modelo> listar() {
        //String sql = "SELECT * FROM modelo JOIN marca ma ON ma.id = id_marca";
        String sql = "SELECT" +
                " mo.id AS modelo_id, " +
                "mo.descricao, " +
                "mo.id_marca, " +
                "mo.categoria, " +
                "ma.nome AS nome_marca, " +
                "mt.potencia, " +
                "mt.tipoCombustivel " +
                "FROM modelo mo " +
                "JOIN marca ma ON mo.id_marca = ma.id " +
                "LEFT JOIN motor mt ON mo.id = mt.id_modelo";
        List<Modelo> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Modelo modelo = new Modelo();
                modelo.setId(resultado.getInt("modelo_id"));
                modelo.setDescricao(resultado.getString("descricao"));

                Marca marca = new Marca();
                marca.setId(resultado.getInt("id_marca"));
                marca.setNome(resultado.getString("nome_marca"));

                Motor motor = new Motor();
                motor.setPotencia(resultado.getInt("potencia"));

                String combustivelStr = resultado.getString("tipoCombustivel");
                if (combustivelStr != null) {
                    motor.seteTipoCombustivel(ETipoCombustivel.valueOf(combustivelStr));
                } else {
                    motor.seteTipoCombustivel(ETipoCombustivel.OUTROS);
                }

                motor.setModelo(modelo);
                modelo.setMarca(marca);
                modelo.setMotor(motor);
                retorno.add(modelo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public Modelo buscar(Modelo modelo) {
        Modelo retorno = buscar(modelo.getId());
        return retorno;
    }

    public Modelo buscar(int id) {
        String sql = "SELECT * FROM modelo WHERE id=?";
        Modelo retorno = new Modelo();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                retorno.setId(resultado.getInt("id"));
                retorno.setDescricao(resultado.getString("nome"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
}
