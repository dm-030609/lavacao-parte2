package br.edu.ifsc.fln.model.dao;

import br.edu.ifsc.fln.model.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrdemDeServicoDAO {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(OrdemDeServico ordem) {
        String sql = "INSERT INTO ordem_servico (numero, total, agenda, desconto, e_status, id_veiculo) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ordem.getId());
            stmt.setDouble(2, ordem.getTotal());
            stmt.setDate(3, new java.sql.Date(ordem.getAgenda().getTime()));
            stmt.setDouble(4, ordem.getDesconto());
            stmt.setString(5, ordem.geteStatus().name());
            stmt.setLong(6, ordem.getVeiculo().getId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            long idGerado = 0;
            if (rs.next()) {
                idGerado = rs.getLong(1);
                ordem.setId((int) idGerado);
                ordem.setNumero(idGerado);

                String sqlNumero = "UPDATE ordem_servico SET numero = ? WHERE id = ?";
                try (PreparedStatement stmtNumero = connection.prepareStatement(sqlNumero)) {
                    stmtNumero.setLong(1, ordem.getNumero());
                    stmtNumero.setLong(2, idGerado);
                    stmtNumero.executeUpdate();
                }
            }

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

    public List<OrdemDeServico> listar() {
        List<OrdemDeServico> lista = new ArrayList<>();
        String sql = "SELECT \n" +
                "  os.*, \n" +
                "  v.id AS veiculo_id, v.placa, \n" +
                "  m.id AS modelo_id, m.descricao AS modelo_desc, m.categoria,\n" +
                "  ma.id AS marca_id, ma.nome AS marca_nome,\n" +
                "  c.id AS cliente_id, c.nome AS cliente_nome, \n" +
                "  IF(pf.id_cliente IS NOT NULL, 'F', 'J') AS cliente_tipo, -- <<< AQUI!\n" +
                "  pf.cpf, pj.cnpj,\n" +
                "  p.id AS pontuacao_id, p.quantidade\n" +
                "FROM ordem_servico os\n" +
                "JOIN veiculo v ON os.id_veiculo = v.id\n" +
                "JOIN modelo m ON v.id_modelo = m.id\n" +
                "JOIN marca ma ON m.id_marca = ma.id\n" +
                "JOIN cliente c ON v.id_cliente = c.id\n" +
                "LEFT JOIN pessoa_fisica pf ON c.id = pf.id_cliente\n" +
                "LEFT JOIN pessoa_juridica pj ON c.id = pj.id_cliente\n" +
                "LEFT JOIN pontuacao p ON c.id_pontuacao = p.id;\n";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OrdemDeServico ordem = new OrdemDeServico();
                ordem.setId(rs.getInt("id"));
                ordem.setNumero(rs.getLong("numero"));
                ordem.setAgenda(rs.getDate("agenda"));
                ordem.setDesconto(rs.getDouble("desconto"));
                ordem.seteStatus(EStatus.valueOf(rs.getString("e_status")));

                Marca marca = new Marca();
                marca.setId(rs.getInt("marca_id"));
                marca.setNome(rs.getString("marca_nome"));

                Modelo modelo = new Modelo();
                modelo.setId(rs.getInt("modelo_id"));
                modelo.setDescricao(rs.getString("modelo_desc"));
                modelo.setMarca(marca);
                modelo.seteCategoria(ECategoria.valueOf(rs.getString("categoria")));

                Cliente cliente;
                String tipo = rs.getString("cliente_tipo");

                if ("F".equalsIgnoreCase(tipo)) {
                    PessoaFisica pf = new PessoaFisica();
                    pf.setCpf(rs.getString("cpf"));
                    cliente = pf;
                } else if ("J".equalsIgnoreCase(tipo)) {
                    PessoaJuridica pj = new PessoaJuridica();
                    pj.setCnpj(rs.getString("cnpj"));
                    cliente = pj;
                } else {
                    throw new SQLException("Tipo de cliente inválido ou não informado.");
                }

                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNome(rs.getString("cliente_nome"));

                // Pontuação
                Pontuacao pontuacao = new Pontuacao();
                pontuacao.setId(rs.getInt("pontuacao_id"));
                pontuacao.setQtd(rs.getInt("quantidade"));
                cliente.setPontuacao(pontuacao);


                Veiculo veiculo = new Veiculo();
                veiculo.setId(rs.getInt("veiculo_id"));
                veiculo.setPlaca(rs.getString("placa"));
                veiculo.setModelo(modelo);
                veiculo.setCliente(cliente);

                ordem.setVeiculo(veiculo);

                ItemOSDAO itemDAO = new ItemOSDAO(connection);
                ordem.setItemsOS(itemDAO.listarPorOrdem(ordem.getId()));

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

            ItemOSDAO itemDAO = new ItemOSDAO(connection);
            itemDAO.removerPorOrdem(ordem.getId());
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
            ItemOSDAO itemDAO = new ItemOSDAO(connection);
            itemDAO.removerPorOrdem(ordem.getId());

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

    public OrdemDeServico buscarPorId(int id) {
        OrdemDeServico ordem = null;

        String sql = "SELECT * FROM ordem_servico WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ordem = new OrdemDeServico();
                ordem.setId(rs.getInt("id"));
                ordem.setNumero(rs.getLong("numero"));
                ordem.setAgenda(rs.getDate("agenda"));
                ordem.setDesconto(rs.getDouble("desconto"));
                ordem.seteStatus(EStatus.valueOf(rs.getString("e_status")));

                VeiculoDAO veiculoDAO = new VeiculoDAO();
                veiculoDAO.setConnection(connection);
                Veiculo veiculo = veiculoDAO.buscar(rs.getInt("id_veiculo"));
                ordem.setVeiculo(veiculo);

                ItemOSDAO itemDAO = new ItemOSDAO(connection);
                List<ItemOS> itens = itemDAO.listarPorOrdem(ordem.getId());
                ordem.setItemsOS(itens);

                ordem.calcularServico();
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ordem;
    }



    public Map<String, Double> buscarTotaisPorMes() {
        Map<String, Double> mapa = new LinkedHashMap<>();

        String sql = """
        SELECT DATE_FORMAT(agenda, '%m/%Y') as mes, SUM(total) as total_mes
        FROM ordem_servico
        WHERE e_status = 'FECHADA'
        GROUP BY mes
        ORDER BY STR_TO_DATE(mes, '%m/%Y')
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String mes = rs.getString("mes");
                double total = rs.getDouble("total_mes");
                mapa.put(mes, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapa;
    }

    public Map<String, Integer> buscarQuantidadePorMes() {
        Map<String, Integer> mapa = new LinkedHashMap<>();

        String sql = """
        SELECT DATE_FORMAT(agenda, '%m/%Y') as mes, COUNT(*) as qtd
        FROM ordem_servico
        WHERE e_status = 'FECHADA'
        GROUP BY mes
        ORDER BY STR_TO_DATE(mes, '%m/%Y')
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String mes = rs.getString("mes");
                int qtd = rs.getInt("qtd");
                mapa.put(mes, qtd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapa;
    }


}
