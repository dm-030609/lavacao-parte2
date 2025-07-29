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

public class FXMLAnchorPaneGraficosMensaisOSController implements Initializable {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private NumberAxis numberAxis;

    @FXML
    private Button btExportarPDF;

    @FXML
    private Label labelTotalOS;

    private OrdemDeServicoDAO ordemDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gerarGraficoQuantidade(); // padrão ao abrir
    }

    private void gerarGraficoQuantidade() {
        Database db = new DatabaseMySQL();
        Connection conn = db.conectar();

        if (conn != null) {
            ordemDAO = new OrdemDeServicoDAO();
            ordemDAO.setConnection(conn);

            Map<String, Integer> dados = ordemDAO.buscarQuantidadePorMes();

            barChart.getData().clear();
            barChart.setTitle("Quantidade de OS por Mês");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total de OS por mês");

            int totalOS = 0;

            for (Map.Entry<String, Integer> entrada : dados.entrySet()) {
                series.getData().add(new XYChart.Data<>(entrada.getKey(), entrada.getValue()));
                totalOS += entrada.getValue();
            }

            barChart.getData().add(series);

            if (labelTotalOS != null) {
                labelTotalOS.setText("Total de Ordens de Serviço: " + totalOS);
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
                String caminhoRelatorio = "/report/grafico_ordens_FINAL_RESOLVIDO.jrxml";
                String titulo = "Relatório de OS por Mês";

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
