package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseMySQL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FXMLAnchorPaneGraficosMensaisController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private NumberAxis numberAxis;

    @FXML
    private Button btGerarGraficoFinanceiro;

    @FXML
    private Button btGerarOsPorMes;

    private OrdemDeServicoDAO ordemDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gerarGraficoFinanceiro(); // padrão ao abrir
    }

    @FXML
    private void handleGerarGraficoFinanceiro() {
        gerarGraficoFinanceiro();
    }

    @FXML
    private void handleGerarGraficoQuantidade() {
        gerarGraficoQuantidade();
    }

    private void gerarGraficoFinanceiro() {
        Database db = new DatabaseMySQL();
        Connection conn = db.conectar();

        if (conn != null) {
            ordemDAO = new OrdemDeServicoDAO();
            ordemDAO.setConnection(conn);

            Map<String, Double> dados = ordemDAO.buscarTotaisPorMes(); // valor R$

            barChart.getData().clear();
            barChart.setTitle("Total R$ por Mês");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total R$ por mês");

            for (Map.Entry<String, Double> entrada : dados.entrySet()) {
                series.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
            }

            barChart.getData().add(series);
            db.desconectar(conn);
        } else {
            System.err.println("Erro ao conectar ao banco de dados.");
        }
    }

    private void gerarGraficoQuantidade() {
        Database db = new DatabaseMySQL();
        Connection conn = db.conectar();

        if (conn != null) {
            ordemDAO = new OrdemDeServicoDAO();
            ordemDAO.setConnection(conn);

            Map<String, Integer> dados = ordemDAO.buscarQuantidadePorMes(); // qtd OS

            barChart.getData().clear();
            barChart.setTitle("Quantidade de OS por Mês");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total de OS por mês");

            for (Map.Entry<String, Integer> entrada : dados.entrySet()) {
                series.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
            }

            barChart.getData().add(series);
            db.desconectar(conn);
        } else {
            System.err.println("Erro ao conectar ao banco de dados.");
        }
    }

    @FXML
    private void handleExportarPDF() {
        Database db = new DatabaseMySQL();
        Connection conn = db.conectar();

        if (conn != null) {
            try {
                // Se quiser diferenciar os relatórios, pode criar um relatorio exclusivo para quantidade também
                String caminhoRelatorio = "src/main/java/br/edu/ifsc/fln/utils/grafico_ordens_VALOR_TOTAL_OK.jrxml";
                System.out.println("DEBUG >> Caminho do relatório: " + caminhoRelatorio);

                JasperReport jasperReport = JasperCompileManager.compileReport(caminhoRelatorio);

                Map<String, Object> parametros = new HashMap<>();

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conn);
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle("Relatório de Vendas por Mês");
                viewer.setVisible(true);

            } catch (Exception e) {
                System.err.println("Erro ao exportar PDF:");
                e.printStackTrace();
            } finally {
                db.desconectar(conn);
            }
        } else {
            System.err.println("Erro ao conectar ao banco de dados.");
        }
    }
}
