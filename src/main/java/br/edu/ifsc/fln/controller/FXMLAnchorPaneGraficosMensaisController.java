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
import javafx.scene.control.Label;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
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

    @FXML private Button btGerarOsPorMes;

    @FXML private Label labelTotalOS;

    private OrdemDeServicoDAO ordemDAO;

    private enum TipoGrafico { VALOR_TOTAL, QUANTIDADE_OS }
    private TipoGrafico graficoAtual = TipoGrafico.VALOR_TOTAL; // padrão

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gerarGraficoFinanceiro(); // padrão ao abrir
    }

    @FXML
    private void handleGerarGraficoFinanceiro() {
        gerarGraficoFinanceiro();
    }

    private void gerarGraficoFinanceiro() {
        graficoAtual = TipoGrafico.VALOR_TOTAL;

        Database db = new DatabaseMySQL();
        Connection conn = db.conectar();

        if (conn != null) {
            ordemDAO = new OrdemDeServicoDAO();
            ordemDAO.setConnection(conn);

            Map<String, Double> dados = ordemDAO.buscarTotaisPorMes(); // valor R$

            barChart.getData().clear();
            barChart.setTitle("Valor Total Arrecadado por Mês");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total R$ por mês");

            double valorTotal = 0.0;

            for (Map.Entry<String, Double> entrada : dados.entrySet()) {
                series.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
                valorTotal += entrada.getValue();
            }

            barChart.getData().add(series);

            if (labelTotalOS != null) {
                labelTotalOS.setText("Valor Total: R$ " + String.format("%.2f", valorTotal).replace(".", ","));
            }

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
                String caminhoRelatorio;
                String titulo;

                if (graficoAtual == TipoGrafico.VALOR_TOTAL) {
                    caminhoRelatorio = "/report/grafico_ordens_VALOR_TOTAL_OK.jrxml";
                    titulo = "Relatório Financeiro por Mês";
                } else {
                    caminhoRelatorio = "/report/grafico_ordens_FINAL_RESOLVIDO.jrxml";
                    titulo = "Relatório de OS por Mês";
                }

                InputStream input = getClass().getResourceAsStream(caminhoRelatorio);

                if (input == null) {
                    throw new RuntimeException("Arquivo do relatório não encontrado: " + caminhoRelatorio);
                }

                JasperReport jasperReport = JasperCompileManager.compileReport(input);

                Map<String, Object> parametros = new HashMap<>();

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conn);
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle(titulo);
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
