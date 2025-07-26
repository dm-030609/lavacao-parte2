package br.edu.ifsc.fln.model.dao;


import br.edu.ifsc.fln.model.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VeiculoDAO {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(Veiculo veiculo) {
        String sql = "INSERT INTO veiculo(placa, observacoes, id_cor, id_modelo, id_cliente) VALUES(?,?,?,?,?);";
        try {
            // Inserindo o veiculo e capturando o ID gerado
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getObservacoes());
            stmt.setInt(3, veiculo.getCor().getId());
            stmt.setInt(4, veiculo.getModelo().getId());
            stmt.setInt(5, veiculo.getCliente().getId());

            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean alterar(Veiculo veiculo) {
        String sql = "UPDATE veiculo SET placa=?, observacoes=?, id_cor=?, id_modelo=?, id_cliente=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getObservacoes());
            stmt.setInt(3, veiculo.getCor().getId());
            stmt.setInt(4, veiculo.getModelo().getId());
            stmt.setInt(5, veiculo.getCliente().getId());
            stmt.setInt(6, veiculo.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean remover(Veiculo veiculo) {
        String sql = "DELETE FROM veiculo WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, veiculo.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void removerPorCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM veiculo WHERE id_cliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }


    public List<Veiculo> listar() {
        //String sql = "SELECT * FROM veiculo JOIN marca ma ON ma.id = id_marca";
        String sql = "SELECT v.*, m.descricao AS modelo_descricao, m.categoria AS modelo_categoria, " +
                "mo.potencia AS motor_potencia, mo.tipoCombustivel AS motor_tipo_combustivel, " +
                "ma.id AS id_marca, ma.nome AS nome_marca , " +
                "c.nome as cor_nome " +
                "FROM veiculo v " +
                "JOIN modelo m ON v.id_modelo = m.id " +
                "JOIN motor mo ON mo.id_modelo = m.id " +
                "JOIN cor c ON v.id_cor = c.id " +
                "JOIN marca ma ON m.id_marca = ma.id " +
                "JOIN cliente cl ON v.id_cliente = cl.id";
        List<Veiculo> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(resultado.getInt("id"));
                veiculo.setPlaca(resultado.getString("placa"));
                veiculo.setObservacoes(resultado.getString("observacoes"));

                Modelo modelo = new Modelo();
                modelo.setId(resultado.getInt("id_modelo"));
                modelo.setDescricao(resultado.getString("modelo_descricao"));
                modelo.seteCategoria(ECategoria.valueOf(resultado.getString("modelo_categoria")));


                Marca marca = new Marca();
                marca.setId(resultado.getInt("id_marca"));
                marca.setNome(resultado.getString("nome_marca"));
                modelo.setMarca(marca);

                Motor motor = new Motor();
                motor.setPotencia(resultado.getInt("motor_potencia"));
                motor.seteTipoCombustivel(ETipoCombustivel.valueOf(resultado.getString("motor_tipo_combustivel")));
                modelo.setMotor(motor);

                veiculo.setModelo(modelo);

                Cor cor = new Cor();
                cor.setId(resultado.getInt("id_cor"));
                cor.setNome(resultado.getString("cor_nome"));
                veiculo.setCor(cor);

                int idCliente = resultado.getInt("id_cliente");
                ClienteDAO clienteDAO = new ClienteDAO();
                clienteDAO.setConnection(this.connection);
                Cliente cliente = clienteDAO.buscar(idCliente);
                veiculo.setCliente(cliente);

                retorno.add(veiculo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public Veiculo buscar(Veiculo veiculo) {
        Veiculo retorno = buscar(veiculo.getId());
        return retorno;
    }

    public Veiculo buscar(int id) {
        String sql = """
        SELECT 
            v.id AS veiculo_id, v.placa,
            m.id AS modelo_id, m.descricao AS modelo_desc, m.categoria,
            ma.id AS marca_id, ma.nome AS marca_nome,
            c.id AS cliente_id, c.nome AS cliente_nome,
            IF(pf.id_cliente IS NOT NULL, 'F', 'J') AS cliente_tipo,
            pf.cpf, pj.cnpj,
            p.id AS pontuacao_id, p.quantidade
        FROM veiculo v
        JOIN modelo m ON v.id_modelo = m.id
        JOIN marca ma ON m.id_marca = ma.id
        JOIN cliente c ON v.id_cliente = c.id
        LEFT JOIN pessoa_fisica pf ON c.id = pf.id_cliente
        LEFT JOIN pessoa_juridica pj ON c.id = pj.id_cliente
        LEFT JOIN pontuacao p ON c.id_pontuacao = p.id
        WHERE v.id = ?;
    """;

        Veiculo veiculo = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Marca
                Marca marca = new Marca();
                marca.setId(rs.getInt("marca_id"));
                marca.setNome(rs.getString("marca_nome"));

                // Modelo
                Modelo modelo = new Modelo();
                modelo.setId(rs.getInt("modelo_id"));
                modelo.setDescricao(rs.getString("modelo_desc"));
                modelo.setMarca(marca);
                modelo.seteCategoria(ECategoria.valueOf(rs.getString("categoria")));

                // Cliente
                Cliente cliente;
                String tipo = rs.getString("cliente_tipo");

                if ("F".equalsIgnoreCase(tipo)) {
                    PessoaFisica pf = new PessoaFisica();
                    pf.setCpf(rs.getString("cpf"));
                    cliente = pf;
                } else {
                    PessoaJuridica pj = new PessoaJuridica();
                    pj.setCnpj(rs.getString("cnpj"));
                    cliente = pj;
                }

                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNome(rs.getString("cliente_nome"));

                // Pontuação
                Pontuacao pontuacao = new Pontuacao();
                pontuacao.setId(rs.getInt("pontuacao_id"));
                pontuacao.setQtd(rs.getInt("quantidade"));
                cliente.setPontuacao(pontuacao);

                // Veículo
                veiculo = new Veiculo();
                veiculo.setId(rs.getInt("veiculo_id"));
                veiculo.setPlaca(rs.getString("placa"));
                veiculo.setModelo(modelo);
                veiculo.setCliente(cliente);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return veiculo;
    }



}
