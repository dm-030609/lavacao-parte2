package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.ItemOSDAO;
import br.edu.ifsc.fln.model.dao.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.PontuacaoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.*;
import br.edu.ifsc.fln.utils.CupomFiscal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroOrdemServicoController implements Initializable {

    @FXML private Button buttonAlterar;
    @FXML private Button buttonInserir;
    @FXML private Button buttonRemover;

    @FXML private Label lbOrdemServicoCliente;
    @FXML private Label lbOrdemServicoData;
    @FXML private Label lbOrdemServicoDesconto;
    @FXML private Label lbOrdemServicoId;
    @FXML private Label lbOrdemServicoNumero;
    @FXML private Label lbOrdemServicoStatus;
    @FXML private Label lbOrdemServicoTotal;
    @FXML private Label lbOrdemServicoPontuacao;
    @FXML private Button buttonCupomFiscal;

    @FXML private TableColumn<OrdemDeServico, Integer> tableColumnOrdemServicoId;
    @FXML private TableColumn<OrdemDeServico, String> tableColumnOrdemServicoData;
    @FXML private TableColumn<OrdemDeServico, String> tableColumnOrdemServicoVeiculo;

    @FXML private TableView<OrdemDeServico> tableViewOrdemServico;




    private List<OrdemDeServico> listaOS;
    private ObservableList<OrdemDeServico> observableListOS;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final OrdemDeServicoDAO osDAO = new OrdemDeServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        osDAO.setConnection(connection);
        carregarTableViewOrdemServico();

        tableViewOrdemServico.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewOrdemServico(newValue)
        );
    }



    public void carregarTableViewOrdemServico() {
        tableColumnOrdemServicoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnOrdemServicoData.setCellValueFactory(new PropertyValueFactory<>("agenda"));
        tableColumnOrdemServicoVeiculo.setCellValueFactory(new PropertyValueFactory<>("veiculo")); // ajustar conforme necessário

        listaOS = osDAO.listar();
        observableListOS = FXCollections.observableArrayList(listaOS);
        tableViewOrdemServico.setItems(observableListOS);
    }

    public void selecionarItemTableViewOrdemServico(OrdemDeServico ordemDeServico) {
        if (ordemDeServico != null) {
            lbOrdemServicoId.setText(String.valueOf(ordemDeServico.getId()));
            lbOrdemServicoNumero.setText(String.valueOf(ordemDeServico.getNumero()));
            lbOrdemServicoData.setText(ordemDeServico.getAgenda().toString());
            lbOrdemServicoDesconto.setText(String.format("%.0f", ordemDeServico.getDesconto()) + " %");
            lbOrdemServicoStatus.setText(ordemDeServico.geteStatus().name());
            lbOrdemServicoTotal.setText(String.format("%.2f", ordemDeServico.getTotal()));
            lbOrdemServicoCliente.setText(ordemDeServico.getVeiculo().getCliente().getNome());
            Pontuacao pontuacao = ordemDeServico.getVeiculo().getCliente().getPontuacao();
            lbOrdemServicoPontuacao.setText(pontuacao != null ? pontuacao.getQtd() + " ponto(s)" : " 0 ponto(s)");



        } else {
            lbOrdemServicoId.setText("");
            lbOrdemServicoNumero.setText("");
            lbOrdemServicoData.setText("");
            lbOrdemServicoDesconto.setText("");
            lbOrdemServicoStatus.setText("");
            lbOrdemServicoTotal.setText("");
        }
    }

    @FXML
    public void handleBtInserir() throws IOException {
        OrdemDeServico ordemDeServico = new OrdemDeServico();
        ordemDeServico.seteStatus(EStatus.ABERTA);

        boolean btConfirmarClicked = showFXMLAnchorPaneCadastroOrdemDeServicoDialog(ordemDeServico);
        if (btConfirmarClicked) {
            osDAO.inserir(ordemDeServico); // precisa garantir que setId está sendo feito aqui
            long idOS = ordemDeServico.getId();

            carregarTableViewOrdemServico();
        }
    }


    @FXML
    public void handleBtAlterar() throws IOException {
        OrdemDeServico osSelecionada = tableViewOrdemServico.getSelectionModel().getSelectedItem();

        if (osSelecionada != null) {
            OrdemDeServico ordemAntes = osDAO.buscarPorId(osSelecionada.getId());
            EStatus statusOriginal = ordemAntes.geteStatus();

            boolean btConfirmarClicked = showFXMLAnchorPaneCadastroOrdemDeServicoDialog(ordemAntes);
            if (btConfirmarClicked) {
                osDAO.alterar(ordemAntes);

                EStatus statusAtual = ordemAntes.geteStatus();

                Cliente cliente = ordemAntes.getVeiculo().getCliente();
                Pontuacao pontuacao = cliente.getPontuacao();
                PontuacaoDAO pontuacaoDAO = new PontuacaoDAO(connection);

                int pontosOS = 0;
                for (ItemOS item : ordemAntes.getItemsOS()) {
                    pontosOS += item.getServico().getPontos();
                }

                if (statusOriginal != EStatus.FECHADA && statusAtual == EStatus.FECHADA) {
                    pontuacao.adicionar(pontosOS);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Pontuação Acumulada");
                    alert.setHeaderText("Ordem de Serviço finalizada com sucesso!");
                    alert.setContentText("Você ganhou +" + pontosOS + " ponto(s).\n" +
                            "Total acumulado: " + pontuacao.getQtd() + " ponto(s).");
                    alert.showAndWait();

                    pontuacaoDAO.atualizar(pontuacao, pontuacao.getId());
                } else if (statusOriginal == EStatus.FECHADA && statusAtual != EStatus.FECHADA) {
                    pontuacao.subtrair(pontosOS);

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Pontuação Removida");
                    alert.setHeaderText("Ordem de Serviço reaberta");
                    alert.setContentText("Foram removidos -" + pontosOS + " ponto(s).\n" +
                            "Total atual: " + pontuacao.getQtd() + " ponto(s).");
                    alert.showAndWait();

                    pontuacaoDAO.atualizar(pontuacao, pontuacao.getId());
                }

                // 🧠 Recarrega OS do banco para garantir dados atualizados
                OrdemDeServico osAtualizada = osDAO.buscarPorId(ordemAntes.getId());

                carregarTableViewOrdemServico(); // atualiza tabela
                selecionarItemTableViewOrdemServico(osAtualizada); // atualiza painel lateral

            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Nenhuma OS selecionada");
            alert.setContentText("Esta operação requer a seleção de uma Ordem de Serviço na tabela.");
            alert.showAndWait();
        }
    }





    @FXML
    public void handleBtExcluir() throws IOException {
        OrdemDeServico ordemDeServico = tableViewOrdemServico.getSelectionModel().getSelectedItem();
        if (ordemDeServico != null) {
            osDAO.remover(ordemDeServico);
            carregarTableViewOrdemServico();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção de uma Ordem de Serviço na tabela.");
            alert.show();
        }
    }

    @FXML
    private void handleBtCupomFiscal() {
        OrdemDeServico osSelecionada = tableViewOrdemServico.getSelectionModel().getSelectedItem();

        if (osSelecionada != null && osSelecionada.geteStatus() == EStatus.FECHADA) {
            CupomFiscal.gerar(osSelecionada.getId());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cupom Fiscal");
            alert.setHeaderText("Seleção inválida");
            alert.setContentText("Selecione uma Ordem de Serviço com status FECHADA para emitir o cupom fiscal.");
            alert.showAndWait();
        }
    }



    private boolean showFXMLAnchorPaneCadastroOrdemDeServicoDialog(OrdemDeServico ordemDeServico) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/FXMLAnchorPaneCadastroOrdemServicoDialog.fxml"));
        AnchorPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Ordem de Serviço");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        FXMLAnchorPaneCadastroOrdemServicoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setOrdemDeServico(ordemDeServico);

        dialogStage.showAndWait();
        return controller.isBtConfirmarClicked();
    }
}
