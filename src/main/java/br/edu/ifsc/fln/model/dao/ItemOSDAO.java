package br.edu.ifsc.fln.model.dao;

import br.edu.ifsc.fln.model.domain.ItemOS;
import br.edu.ifsc.fln.model.domain.Servico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemOSDAO {

    private final Connection connection;

    public ItemOSDAO(Connection connection) {
        this.connection = connection;
    }

    // INSERIR item_os ligado a uma Ordem de Serviço
    public void inserir(ItemOS item, long idOrdemServico) throws SQLException {
        String sql = "INSERT INTO item_os (valor_servico, observacoes, id_servico, id_ordem_servico) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, item.getValorServico());
            stmt.setString(2, item.getObservacoes());
            stmt.setInt(3, item.getServico().getId());
            stmt.setLong(4, idOrdemServico);
            stmt.executeUpdate();

            // Recupera o ID gerado (opcional)
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        }
    }

    // LISTAR todos os itens de uma determinada ordem de serviço
    public List<ItemOS> listarPorOrdem(long idOrdemServico) throws SQLException {
        List<ItemOS> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_os WHERE id_ordem_servico = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idOrdemServico);
            ResultSet rs = stmt.executeQuery();

            ServicoDAO servicoDAO = new ServicoDAO(); // ⚠️ Certifique-se de injetar/definir conexão abaixo
            servicoDAO.setConnection(connection);

            while (rs.next()) {
                ItemOS item = new ItemOS();
                item.setId(rs.getInt("id"));
                item.setValorServico(rs.getDouble("valor_servico"));
                item.setObservacoes(rs.getString("observacoes"));

                int idServico = rs.getInt("id_servico");
                Servico servico = servicoDAO.buscar(idServico); // 🔥 Busca completa, nome incluso
                item.setServico(servico);

                itens.add(item);
            }
        }

        return itens;
    }


    // REMOVER todos os itens vinculados a uma OS (útil para edição ou exclusão em cascata)
    public void removerPorOrdem(long idOrdemServico) throws SQLException {
        String sql = "DELETE FROM item_os WHERE id_ordem_servico = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idOrdemServico);
            stmt.executeUpdate();
        }
    }
}
