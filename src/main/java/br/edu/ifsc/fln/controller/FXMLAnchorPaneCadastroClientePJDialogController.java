/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.domain.Cliente;
import br.edu.ifsc.fln.model.domain.PessoaFisica;
import br.edu.ifsc.fln.model.domain.PessoaJuridica;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroClientePJDialogController implements Initializable {

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    @FXML
    private DatePicker dpDataCadastro;


    @FXML
    private TextField tfCelular;


    @FXML
    private TextField tfCnpj;

    @FXML
    private TextField tfInscricaoEstadual;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfNome;

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Cliente cliente;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        if(cliente != null){
            this.tfNome.setText(this.cliente.getNome());
            this.tfEmail.setText(this.cliente.getEmail());
            this.tfCelular.setText(this.cliente.getCelular());

            if (this.cliente.getDataCadastro() != null) {
                dpDataCadastro.setValue(new java.util.Date(cliente.getDataCadastro().getTime())
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }


            if (cliente instanceof PessoaJuridica pj) {
                this.tfCnpj.setText(pj.getCnpj());
                this.tfInscricaoEstadual.setText(pj.getInscricaoEstadual());

            }

        }else {
            // Cliente novo → data atual como padrão
            dpDataCadastro.setValue(LocalDate.now());
        }


    }


    @FXML
    public void handleBtConfirmar() {
        if (validarEntradaDeDados()) {
            cliente.setNome(tfNome.getText());
            cliente.setEmail(tfEmail.getText());
            cliente.setCelular(tfCelular.getText());
            cliente.setDataCadastro(Date.from(dpDataCadastro.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            if (cliente instanceof PessoaJuridica pj) {
                pj.setCnpj(tfCnpj.getText());
                pj.setInscricaoEstadual(tfInscricaoEstadual.getText());
            }

            this.cliente = cliente;

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
        if (this.tfNome.getText() == null || this.tfNome.getText().length() == 0) {
            errorMessage += "Nome inválido.\n";
        }

        if (this.tfCnpj.getText() == null || this.tfCnpj.getText().length() == 0) {
            errorMessage += "CPF inválido.\n";
        }

        if (tfInscricaoEstadual.getText() == null || tfInscricaoEstadual.getText().isBlank()) {
            errorMessage += "Inscrição estadual inválida!\n";
        }

        if (this.tfCelular.getText() == null || this.tfCelular.getText().length() == 0) {
            errorMessage += "Telefone inválido.\n";
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
