package br.edu.ifsc.fln.utils;

import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseMySQL;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class CupomFiscal {

    public static void gerar(int idOrdemServico) {
        Database database = new DatabaseMySQL();
        try (Connection conn = database.conectar()) {

            // Caminho do arquivo .jrxml dentro da pasta resources/relatorios
            InputStream input = CupomFiscal.class.getResourceAsStream("/report/Cupom_Fiscal_OS.jrxml");



            if (input == null) {
                throw new RuntimeException("Arquivo JRXML não encontrado em /report/Cupom_Fiscal_OS.jrxml");
            }

            JasperReport report = JasperCompileManager.compileReport(input);

            // Parâmetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_OS", idOrdemServico);

            // Preenchendo o relatório
            JasperPrint print = JasperFillManager.fillReport(report, parametros, conn);

            // Visualizar na tela
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Cupom Fiscal da Ordem de Serviço");
            viewer.setVisible(true);

            // (opcional) exportar PDF direto:
            // JasperExportManager.exportReportToPdfFile(print, "cupom_os_" + idOrdemServico + ".pdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
