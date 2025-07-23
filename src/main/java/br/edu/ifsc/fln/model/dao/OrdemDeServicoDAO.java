package br.edu.ifsc.fln.model.dao;

import br.edu.ifsc.fln.model.domain.EStatus;
import br.edu.ifsc.fln.model.domain.ItemOS;
import br.edu.ifsc.fln.model.domain.OrdemDeServico;
import br.edu.ifsc.fln.model.dao.ItemOSDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdemDeServicoDAO {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // INSERIR uma nova OS com seus itens
    public boolean inserir(OrdemDeServico ordem) {
        String sql = "INSERT INTO ordem_servico (numero, total, agenda, desconto, e_status, id_veiculo) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, ordem.getNumero());
            stmt.setDouble(2, ordem.getTotal());
            stmt.setDate(3, new java.sql.Date(ordem.getAgenda().getTime()));
            stmt.setDouble(4, ordem.getDesconto());
            stmt.setString(5, ordem.geteStatus().name());
            stmt.setLong(6, ordem.getVeiculo().getId());

            stmt.executeUpdate();

            // Recupera o ID gerado da OS
            ResultSet rs = stmt.getGeneratedKeys();
            long idGerado = 0;
            if (rs.next()) {
                idGerado = rs.getLong(1);
                ordem.setId((int) idGerado);
            }

            // Insere os itens com o DAO
            ItemOSDAO itemDAO = new ItemOSDAO(connection);
            for (ItemOS item : ordem.getItemsOS()) {
                itemDAO.inserir(item, idGerado);
            }

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // LISTAR todas as OS com seus itens
    public List<OrdemDeServico> listar() {
        List<OrdemDeServico> lista = new ArrayList<>();
        String sql = "SELECT * FROM ordem_servico";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OrdemDeServico ordem = new OrdemDeServico();
                ordem.setId(rs.getInt("id"));
                ordem.setNumero(rs.getLong("numero"));
                ordem.setAgenda(rs.getDate("agenda"));
                ordem.setDesconto(rs.getDouble("desconto"));
                ordem.seteStatus(EStatus.valueOf(rs.getString("e_status")));
                // o total será recalculado via itens

                // Carrega os itens da OS
                ItemOSDAO itemDAO = new ItemOSDAO(connection);
                ordem.setItemsOS(itemDAO.listarPorOrdem(ordem.getId()));

                // Recalcula total com base nos itens
                ordem.calcularServico();

                lista.add(ordem);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    public boolean alterar(OrdemDeServico ordem) {
        String sql = "UPDATE ordem_servico SET numero = ?, total = ?, agenda = ?, desconto = ?, e_status = ?, id_veiculo = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, ordem.getNumero());
            stmt.setDouble(2, ordem.getTotal());
            stmt.setDate(3, new java.sql.Date(ordem.getAgenda().getTime()));
            stmt.setDouble(4, ordem.getDesconto());
            stmt.setString(5, ordem.geteStatus().name());
            stmt.setLong(6, ordem.getVeiculo().getId());
            stmt.setInt(7, ordem.getId());

            stmt.executeUpdate();

            // Atualiza os itens: primeiro remove os antigos
            ItemOSDAO itemDAO = new ItemOSDAO(connection);
            itemDAO.removerPorOrdem(ordem.getId());

            // Depois insere os novos
            for (ItemOS item : ordem.getItemsOS()) {
                itemDAO.inserir(item, ordem.getId());
            }

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public boolean remover(OrdemDeServico ordem) {
        try {
            // Remove itens primeiro (por segurança, mesmo se tiver ON DELETE CASCADE)
            ItemOSDAO itemDAO = new ItemOSDAO(connection);
            itemDAO.removerPorOrdem(ordem.getId());

            // Remove a ordem de serviço
            String sql = "DELETE FROM ordem_servico WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, ordem.getId());
            stmt.executeUpdate();

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
