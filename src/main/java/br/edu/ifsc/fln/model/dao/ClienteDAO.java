/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsc.fln.model.dao;

import br.edu.ifsc.fln.model.domain.Cliente;
import br.edu.ifsc.fln.model.domain.PessoaFisica;
import br.edu.ifsc.fln.model.domain.PessoaJuridica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mpisc
 */
public class ClienteDAO {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente(nome, celular, email, data_cadastro) VALUES(?, ?, ?, ?)";
        String sqlPF = "INSERT INTO pessoa_fisica(id_cliente, cpf, data_nascimento) VALUES((SELECT max(id) FROM cliente), ?, ?)";
        String sqlPJ = "INSERT INTO pessoa_juridica(id_cliente, cnpj, inscricao_estadual) VALUES((SELECT max(id) FROM cliente), ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCelular());
            stmt.setString(3, cliente.getEmail());
            stmt.setDate(4, new java.sql.Date(cliente.getDataCadastro().getTime()));
            stmt.execute();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cliente.setId(rs.getInt(1));
            }
            rs.close();

            if (cliente instanceof PessoaFisica) {
                try (PreparedStatement stmtPF = this.connection.prepareStatement(sqlPF)) {
                    stmtPF.setString(1, ((PessoaFisica) cliente).getCpf());
                    //stmtPF.setDate(2, new java.sql.Date(((PessoaFisica) cliente).getDataNascimento().getTime()));
                    java.util.Date utilDate = ((PessoaFisica) cliente).getDataNascimento();
                    if (utilDate != null) {
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                        stmtPF.setDate(2, sqlDate);
                    } else {
                        stmtPF.setNull(2, java.sql.Types.DATE);
                    }
                    stmtPF.execute();
                }
            } else if (cliente instanceof PessoaJuridica) {
                try (PreparedStatement stmtPJ = this.connection.prepareStatement(sqlPJ)) {
                    stmtPJ.setString(1, ((PessoaJuridica) cliente).getCnpj());
                    stmtPJ.setString(2, ((PessoaJuridica) cliente).getInscricaoEstadual());
                    stmtPJ.execute();
                }
            }
            return true;
        }catch(SQLException ex){
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }


public boolean alterar(Cliente cliente) {
    String sql = "UPDATE cliente SET nome=?, celular=?, email=?, data_cadastro=? WHERE id=?";
    String sqlPF = "UPDATE pessoa_fisica SET cpf=?, data_nascimento=? WHERE id_cliente = ?";
    String sqlPJ = "UPDATE pessoa_juridica SET cnpj=?, inscricao_estadual=? WHERE id_cliente = ?";
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getCelular());
        stmt.setString(3, cliente.getEmail());
        stmt.setDate(4, new java.sql.Date(cliente.getDataCadastro().getTime()));
        stmt.setInt(5, cliente.getId());
        stmt.execute();

        if (cliente instanceof PessoaFisica) {
            try (PreparedStatement stmtPF = this.connection.prepareStatement(sqlPF)) {
                stmtPF.setString(1, ((PessoaFisica) cliente).getCpf());
                stmtPF.setDate(2, new java.sql.Date(((PessoaFisica) cliente).getDataNascimento().getTime()));
                stmtPF.setInt(3, cliente.getId());
                stmtPF.execute();
            }
        } else if (cliente instanceof PessoaJuridica) {
            try (PreparedStatement stmtPJ = this.connection.prepareStatement(sqlPJ)) {
                stmtPJ.setString(1, ((PessoaJuridica) cliente).getCnpj());
                stmtPJ.setString(2, ((PessoaJuridica) cliente).getInscricaoEstadual());
                stmtPJ.setInt(3, cliente.getId());
                stmtPJ.execute();
            }
        }
    }catch (SQLException ex) {
        Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        return false;
    }
        return true;
    }


public boolean remover(Cliente cliente) {
    String sql = "DELETE FROM cliente WHERE id=?";
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, cliente.getId());
        stmt.execute();
        return true;
    } catch (SQLException ex) {
        Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        return false;
    }
}

public List<Cliente> listar() {
    String sql = "SELECT * FROM cliente c LEFT JOIN pessoa_fisica pf ON c.id=pf.id_cliente LEFT JOIN pessoa_juridica pj ON c.id=pj.id_cliente";
    List<Cliente> retorno = new ArrayList<>();
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        ResultSet resultado = stmt.executeQuery();
        while (resultado.next()) {
            Cliente cliente = populateVO(resultado);
            retorno.add(cliente);
        }
    } catch (SQLException ex) {
        Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return retorno;
}

public Cliente buscar(int cliente) {
    String sql = "SELECT c.*, pf.cpf, pf.data_nascimento, pj.cnpj, pj.inscricao_estadual " +
            "FROM cliente c " +
            "LEFT JOIN pessoa_fisica pf ON pf.id_cliente = c.id " +
            "LEFT JOIN pessoa_juridica pj ON pj.id_cliente = c.id " +
            "WHERE c.id = ?";
    try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, cliente);
        ResultSet resultado = stmt.executeQuery();
        if (resultado.next()) {
            return populateVO(resultado);
        }
    } catch (SQLException ex) {
        Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
}

private Cliente populateVO(ResultSet rs) throws SQLException {
    Cliente cliente;

    if (rs.getString("cnpj") != null && !rs.getString("cnpj").isEmpty()) {
        cliente = new PessoaJuridica();
        ((PessoaJuridica) cliente).setCnpj(rs.getString("cnpj"));
        ((PessoaJuridica) cliente).setInscricaoEstadual(rs.getString("inscricao_estadual"));
    } else {
        cliente = new PessoaFisica();
        ((PessoaFisica) cliente).setCpf(rs.getString("cpf"));
        ((PessoaFisica) cliente).setDataNascimento(rs.getDate("data_nascimento"));
    }
    cliente.setId(rs.getInt("id"));
    cliente.setNome(rs.getString("nome"));
    cliente.setCelular(rs.getString("celular"));
    cliente.setEmail(rs.getString("email"));
    cliente.setDataCadastro(rs.getDate("data_cadastro"));
    return cliente;
}
}
