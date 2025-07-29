package br.edu.ifsc.fln.utils;

import br.edu.ifsc.fln.model.database.DatabaseMySQL;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;

public class Relatorio {

    public static void abrirRelatorio(String titulo, String caminhoRelatorio) {
        try {
            JasperReport relatorio = (JasperReport) JRLoader.loadObject(Relatorio.class.getResource(caminhoRelatorio));
            JasperPrint impressao = JasperFillManager.fillReport(relatorio, new HashMap<>(), Conexao.getConnection());
            JasperViewer viewer = new JasperViewer(impressao, false);
            viewer.setTitle(titulo);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gerarGraficoVendasPorMes() {
        try {
            Connection conexao = new DatabaseMySQL().conectar();

            // Caminho real para onde está o .jrxml fora de /resources
            String basePath = System.getProperty("user.dir");
            String caminhoCompleto = basePath + "/src/main/java/br/edu/ifsc/fln/utils/grafico_ordens_VALOR_TOTAL_OK.jrxml";

            System.out.println("DEBUG >> Caminho real do relatório: " + caminhoCompleto);

            FileInputStream input = new FileInputStream(caminhoCompleto);

            JasperReport report = JasperCompileManager.compileReport(input);
            JasperPrint print = JasperFillManager.fillReport(report, null, conexao);

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Relatório de Vendas por Mês");
            viewer.setVisible(true);

            conexao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void gerarGraficoQuantidadeOS() {
        try {
            Connection conexao = new DatabaseMySQL().conectar();
            String caminho = "/report/grafico_ordens_FINAL_RESOLVIDO.jrxml";

            InputStream input = Relatorio.class.getResourceAsStream("/report/grafico_ordens_FINAL_RESOLVIDO.jrxml");
            JasperReport report = JasperCompileManager.compileReport(input);
            JasperPrint print = JasperFillManager.fillReport(report, null, conexao);
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Gráfico de Ordens de Serviço por Mês");
            viewer.setVisible(true);

            conexao.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
