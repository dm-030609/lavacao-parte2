/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.ClienteDAO;
import br.edu.ifsc.fln.model.dao.CorDAO;
import br.edu.ifsc.fln.model.dao.MarcaDAO;
import br.edu.ifsc.fln.model.dao.ModeloDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroVeiculoDialogController implements Initializable {

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    @FXML
    private ChoiceBox<Cliente> cbCliente;

    @FXML
    private ChoiceBox<Cor> cbCor;

    @FXML
    private ChoiceBox<Modelo> cbModelo;

    @FXML
    private TextField tfObservacoes;

    @FXML
    private TextField tfPlaca;

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Veiculo veiculo;

    private Modelo modelo;

    private Cor cor;

    private final ModeloDAO modeloDAO = new ModeloDAO();
    private final CorDAO corDAO = new CorDAO();

    private final ClienteDAO clienteDAO = new ClienteDAO();

    private ObservableList<Modelo> observableListModelos;
    private ObservableList<Cor> observableListCores;

    private ObservableList<Cliente> observableListClientes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        carregarModelos();
        carregarCores();
        carregarClientes();

    }

    public boolean isBtConfirmarClicked() {
        return btConfirmarClicked;
    }

    public void setBtConfirmarClicked(boolean btConfirmarClicked) {
        this.btConfirmarClicked = btConfirmarClicked;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void carregarClientes() {
        Connection conn = new DatabaseFactory().getDatabase("mysql").conectar();
        clienteDAO.setConnection(conn);
        List<Cliente> listaClientes = clienteDAO.listar();
        observableListClientes = FXCollections.observableArrayList(listaClientes);
        cbCliente.setItems(observableListClientes);
    }

    public void carregarModelos() {
        Connection conn = new DatabaseFactory().getDatabase("mysql").conectar();
        modeloDAO.setConnection(conn);
        List<Modelo> listaModelos = modeloDAO.listar();
        observableListModelos = FXCollections.observableArrayList(listaModelos);
        cbModelo.setItems(observableListModelos);

    }

    public void carregarCores() {
        Connection conn = new DatabaseFactory().getDatabase("mysql").conectar();
        corDAO.setConnection(conn);
        List<Cor> listaCores = corDAO.listar();
        observableListCores = FXCollections.observableArrayList(listaCores);
        cbCor.setItems(observableListCores);

    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
        if (veiculo != null) {
            tfPlaca.setText(veiculo.getPlaca());
            tfObservacoes.setText(veiculo.getObservacoes());
            cbModelo.setValue(veiculo.getModelo());
            cbCor.setValue(veiculo.getCor());
            cbCliente.setValue(veiculo.getCliente());
        }
    }


    @FXML
    public void handleBtConfirmar() {

        if(validarEntradaDeDados()) {

            if(veiculo == null) {
                veiculo = new Veiculo();
            }

            veiculo.setPlaca(tfPlaca.getText());
            veiculo.setObservacoes(tfObservacoes.getText());
            veiculo.setModelo(cbModelo.getValue());
            veiculo.setCor(cbCor.getValue());
            veiculo.setCliente(cbCliente.getValue());

            btConfirmarClicked = true;
            dialogStage.close();

        }
    }

    @FXML
    public void handleBtCancelar() {
        dialogStage.close();
    }

    //método para validar a entrada de dados
    private boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (this.tfPlaca.getText() == null || this.tfPlaca.getText().length() == 0) {
            errorMessage += "Placa inválida.\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            //exibindo uma mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Corrija os campos inválidos!");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }

}
    

