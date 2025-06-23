/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.domain.Cliente;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.ResourceBundle;

import br.edu.ifsc.fln.model.domain.PessoaFisica;
import br.edu.ifsc.fln.model.domain.PessoaJuridica;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroClientePFDialogController implements Initializable {

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    @FXML
    private DatePicker dpDataCadastro;

    @FXML
    private DatePicker dpDataNascimento;

    @FXML
    private TextField tfCelular;

    @FXML
    private TextField tfCpf;

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
                //dpDataCadastro.setValue(((java.sql.Date) cliente.getDataCadastro()).toLocalDate());
                dpDataCadastro.setValue(new java.util.Date(cliente.getDataCadastro().getTime())
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            }

            if(cliente instanceof PessoaFisica pf) {
                this.tfCpf.setText(pf.getCpf());
                Date dataNasc = ((PessoaFisica) this.cliente).getDataNascimento();
                if (dataNasc != null) {
                    dpDataNascimento.setValue(((java.sql.Date) dataNasc).toLocalDate());
                }
            }


        }else {
            // Cliente novo → data atual como padrão
            dpDataCadastro.setValue(LocalDate.now());
        }


    }


    @FXML
    public void handleBtConfirmar() {
        dpDataNascimento.getEditor().commitValue();
        if (validarEntradaDeDados()) {
            cliente.setNome(tfNome.getText());
            cliente.setEmail(tfEmail.getText());
            cliente.setCelular(tfCelular.getText());
            cliente.setDataCadastro(Date.from(dpDataCadastro.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));


            if(cliente instanceof PessoaFisica pf) {
                pf.setCpf(tfCpf.getText());
                if (dpDataNascimento.getValue() != null) {
                    pf.setDataNascimento(java.sql.Date.valueOf(dpDataNascimento.getValue()));
                }
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

        if (this.tfCpf.getText() == null || this.tfCpf.getText().length() == 0) {
            errorMessage += "CPF inválido.\n";
        }

        if (this.tfCelular.getText() == null || this.tfCelular.getText().length() == 0) {
            errorMessage += "Telefone inválido.\n";
        }

        try {
            String texto = dpDataNascimento.getEditor().getText();
            if (!texto.isBlank()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate parsedDate = LocalDate.parse(texto, formatter);
                dpDataNascimento.setValue(parsedDate); // aplica valor real no campo
            }
        } catch (DateTimeParseException e) {
            errorMessage += "Data de nascimento inválida!\n";
        }

        if (dpDataNascimento.getValue() == null) {
            errorMessage += "Data de nascimento é obrigatória!\n";
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
