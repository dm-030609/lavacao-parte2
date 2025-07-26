package br.edu.ifsc.fln.model.dao;

import br.edu.ifsc.fln.model.database.DatabaseMySQL;
import br.edu.ifsc.fln.model.domain.Pontuacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PontuacaoDAO {
    private final Connection connection;

    public PontuacaoDAO(Connection connection) {
        this.connection = connection;
    }

    public int inserir(Pontuacao pontuacao) throws SQLException {
        String sql = "INSERT INTO pontuacao (quantidade) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pontuacao.getQtd());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean atualizar(Pontuacao pontuacao, int id) {
        String sql = "UPDATE pontuacao SET quantidade = ? WHERE id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, pontuacao.getQtd());
            stmt.setInt(2, pontuacao.getId());
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Pontuacao buscarPorId(int id) {
        Pontuacao pontuacao = null;
        String sql = "SELECT * FROM pontuacao WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pontuacao = new Pontuacao();
                pontuacao.setId(rs.getInt("id"));
                pontuacao.setQtd(rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return pontuacao;
    }

}


