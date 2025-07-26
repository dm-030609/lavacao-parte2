package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.ServicoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.Servico;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroServicoController implements Initializable {

    @FXML
    private Button btAlterar;

    @FXML
    private Button btExcluir;

    @FXML
    private Button btInserir;

    @FXML
    private Label lbServicoDescricao;

    @FXML
    private Label lbServicoId;

    @FXML
    private Label lbServicoPontos;

    @FXML
    private Label lbServicoValor;

    @FXML
    private TableColumn<Servico, String> tableColumnServicoNome;

    @FXML
    private TableView<Servico> tableViewServicos;

    private List<Servico> listaServicos;
    private ObservableList<Servico> observableListServicos;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ServicoDAO servicoDAO = new ServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoDAO.setConnection(connection);
        carregarTableViewServico();

        tableViewServicos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewServicos(newValue));
    }

    public void carregarTableViewServico() {
        tableColumnServicoNome.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        listaServicos = servicoDAO.listar();
        observableListServicos = FXCollections.observableArrayList(listaServicos);
        tableViewServicos.setItems(observableListServicos);
    }

    public void selecionarItemTableViewServicos(Servico servico) {
        if (servico != null) {
            lbServicoId.setText(String.valueOf(servico.getId()));
            lbServicoDescricao.setText(servico.getDescricao());
            lbServicoValor.setText(String.valueOf(servico.getValor()));
            lbServicoPontos.setText(String.valueOf(servico.getCategoria().getPontos()));
        } else {
            lbServicoId.setText("");
            lbServicoDescricao.setText("");
            lbServicoValor.setText("");
            lbServicoPontos.setText("");
        }
    }

    @FXML
    public void handleBtInserir() throws IOException {
        Servico novoServico = new Servico();
        boolean btConfirmarClicked = showFXMLAnchorPaneCadastroServicoDialog(novoServico);
        if (btConfirmarClicked) {
            servicoDAO.inserir(novoServico);
            carregarTableViewServico();
            tableViewServicos.getSelectionModel().selectLast();
        }
    }

    @FXML
    public void handleBtAlterar() throws IOException {
        Servico servicoSelecionado = tableViewServicos.getSelectionModel().getSelectedItem();
        if (servicoSelecionado != null) {
            // Criar cópia temporária para evitar efeitos colaterais
            Servico servicoClone = new Servico();
            servicoClone.setId(servicoSelecionado.getId());
            servicoClone.setDescricao(servicoSelecionado.getDescricao());
            servicoClone.setValor(servicoSelecionado.getValor());

            boolean btConfirmarClicked = showFXMLAnchorPaneCadastroServicoDialog(servicoClone);
            if (btConfirmarClicked) {
                servicoDAO.alterar(servicoClone);
                carregarTableViewServico();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção de um serviço na tabela.");
            alert.show();
        }
    }


    @FXML
    public void handleBtExcluir() {
        Servico servicoSelecionado = tableViewServicos.getSelectionModel().getSelectedItem();
        if (servicoSelecionado != null) {
            servicoDAO.remover(servicoSelecionado);
            carregarTableViewServico();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde um Serviço na tabela ao lado");
            alert.show();
        }
    }

    private boolean showFXMLAnchorPaneCadastroServicoDialog(Servico servico) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroServicoController.class.getResource("/view/FXMLAnchorPaneCadastroServicoDialog.fxml"));
        AnchorPane page = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Serviço");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        FXMLAnchorPaneCadastroServicoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setServico(servico);

        dialogStage.showAndWait();

        return controller.isBtConfirmarClicked();
    }
}
