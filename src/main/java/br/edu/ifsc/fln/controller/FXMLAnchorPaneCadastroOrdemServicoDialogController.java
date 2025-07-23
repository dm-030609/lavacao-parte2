package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.VeiculoDAO;
import br.edu.ifsc.fln.model.dao.ServicoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.List;

public class FXMLAnchorPaneCadastroOrdemServicoDialogController {

    @FXML private ComboBox<Veiculo> comboBoxPlaca;
    @FXML private TextField textFieldCliente;
    @FXML private TextField textFieldModelo;
    @FXML private TextField textFieldMarca;
    @FXML private TextField textFieldCategoria;
    @FXML private DatePicker datePickerData;

    @FXML private ComboBox<Servico> comboBoxServico;
    @FXML private TextField textFieldValorServico;
    @FXML private TextField textFieldObservacoes;
    @FXML private Button buttonAdicionarServico;

    @FXML private TableView<ItemOS> tableViewItens;
    @FXML private TableColumn<ItemOS, String> colServico;
    @FXML private TableColumn<ItemOS, String> colValor;
    @FXML private TableColumn<ItemOS, String> colObservacoes;

    @FXML private TextField textFieldDesconto;
    @FXML private TextField textFieldValorTotal;
    @FXML private ComboBox<String> comboBoxStatus;
    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;

    @FXML
    private void handleButtonConfirmar() {
        btConfirmarClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleButtonCancelar() {
        dialogStage.close();
    }

    private final ObservableList<ItemOS> listaItens = FXCollections.observableArrayList();
    private final OrdemDeServicoDAO osDAO = new OrdemDeServicoDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();



    private Stage dialogStage;

    private OrdemDeServico ordemDeServico;
    private boolean btConfirmarClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isBtConfirmarClicked() {
        return btConfirmarClicked;
    }

    public void setBtConfirmarClicked(boolean btConfirmarClicked) {
        this.btConfirmarClicked = btConfirmarClicked;
    }

    @FXML
    public void initialize() {
        veiculoDAO.setConnection(connection);
        servicoDAO.setConnection(connection);

        carregarVeiculos();
        carregarServicos();

        comboBoxStatus.setItems(FXCollections.observableArrayList("PENDENTE", "FINALIZADA"));

        colServico.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServico().getDescricao()));
        colValor.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getValorServico())));
        colObservacoes.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getObservacoes()));

        tableViewItens.setItems(listaItens);

        comboBoxPlaca.setOnAction(e -> preencherCamposVeiculo());
        comboBoxServico.setOnAction(e -> preencherValorServico());
    }


    public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
        this.ordemDeServico = ordemDeServico;
    }

    private void carregarVeiculos() {
        List<Veiculo> lista = veiculoDAO.listar();
        comboBoxPlaca.setItems(FXCollections.observableArrayList(lista));
    }

    private void carregarServicos() {
        List<Servico> lista = servicoDAO.listar();
        comboBoxServico.setItems(FXCollections.observableArrayList(lista));
    }

    private void preencherCamposVeiculo() {
        Veiculo veiculo = comboBoxPlaca.getSelectionModel().getSelectedItem();
        if (veiculo != null) {
            textFieldCliente.setText(veiculo.getCliente().getNome());
            textFieldModelo.setText(veiculo.getModelo().getDescricao());
            textFieldMarca.setText(veiculo.getModelo().getMarca().getNome());
            textFieldCategoria.setText(veiculo.getModelo().geteCategoria().toString());
        }
    }

    private void preencherValorServico() {
        Servico s = comboBoxServico.getSelectionModel().getSelectedItem();
        if (s != null) {
            textFieldValorServico.setText(String.format("%.2f", s.getValor()));
        } else {
            textFieldValorServico.clear();
        }
    }

    @FXML
    private void handleButtonAdicionarServico() {
        Servico s = comboBoxServico.getSelectionModel().getSelectedItem();
        if (s != null) {
            ItemOS item = new ItemOS();
            item.setServico(s);
            item.setValorServico(s.getValor());
            item.setObservacoes(textFieldObservacoes.getText());

            listaItens.add(item);
            tableViewItens.refresh();
        }
    }
}
