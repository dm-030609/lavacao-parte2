/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.MarcaDAO;
import br.edu.ifsc.fln.model.dao.ModeloDAO;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.*;
import javafx.collections.FXCollections;
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
public class FXMLAnchorPaneCadastroModeloDialogController implements Initializable {

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    @FXML
    private TextField tfDescricao;

    @FXML
    private ChoiceBox<Marca> cbMarca;

    @FXML
    private ChoiceBox<ETipoCombustivel> cbTipoCombustivel;

    @FXML
    private ChoiceBox<ECategoria> cbCategoria;

    @FXML
    private TextField tfPotencia;


    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Modelo modelo;

    private Motor motor;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cbMarca.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && cbMarca.getItems().isEmpty()) {
                carregarMarcas();
            }
        });

        cbTipoCombustivel.setItems(FXCollections.observableArrayList(ETipoCombustivel.values()));
        cbCategoria.setItems(FXCollections.observableArrayList(ECategoria.values()));
        cbTipoCombustivel.setValue(ETipoCombustivel.GNV); // ou outro valor default
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

    private void carregarMarcas() {
        MarcaDAO marcaDAO = new MarcaDAO();
        Connection conn = new DatabaseFactory().getDatabase("mysql").conectar();
        marcaDAO.setConnection(conn);

        List<Marca> lista = marcaDAO.listar();
        cbMarca.setItems(FXCollections.observableArrayList(lista));
    }



    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
        this.motor = modelo.getMotor();
        if (modelo != null) {
            tfDescricao.setText(modelo.getDescricao());
            cbCategoria.setValue(modelo.geteCategoria());
            cbMarca.setValue(modelo.getMarca());
            cbTipoCombustivel.setValue(modelo.getMotor().geteTipoCombustivel());
        }
    }
    

    @FXML
    public void handleBtConfirmar() {
        if (validarEntradaDeDados()) {
            if (tfDescricao.getText().isEmpty() || cbMarca.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Campos obrigatórios");
                alert.setHeaderText("Informação incompleta");
                alert.setContentText("Informe o nome do modelo e selecione uma marca.");
                alert.show();
                return;
            }

            if (cbTipoCombustivel.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erro");
                alert.setHeaderText("Tipo de combustível não selecionado");
                alert.setContentText("Por favor, selecione o tipo de combustível.");
                alert.showAndWait();
                return;
            }

            modelo.setDescricao(tfDescricao.getText());
            modelo.setMarca(cbMarca.getValue());
            modelo.seteCategoria(cbCategoria.getValue());


            motor.setPotencia(Integer.parseInt(tfPotencia.getText()));
            motor.seteTipoCombustivel(cbTipoCombustivel.getValue());
            motor.setModelo(modelo);

            modelo.setMotor(motor);

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
        if (this.tfDescricao.getText() == null || this.tfDescricao.getText().length() == 0) {
            errorMessage += "Descrição inválida.\n";
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
