/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.domain.ECategoria;
import br.edu.ifsc.fln.model.domain.Marca;
import br.edu.ifsc.fln.model.domain.Servico;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroServicoDialogController implements Initializable {

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    @FXML
    private TextField tfServicoDescricao;

    @FXML
    private ComboBox<ECategoria> cbCategoria;

    @FXML
    private TextField tfServicoPontos;

    @FXML
    private TextField tfServicoValor;
    
    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Servico servico;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Preenche a ComboBox com os valores do enum
        cbCategoria.setItems(FXCollections.observableArrayList(ECategoria.values()));

        // Atualiza os pontos automaticamente quando muda a categoria
        cbCategoria.setOnAction(e -> {
            ECategoria categoria = cbCategoria.getValue();
            if (categoria != null) {
                tfServicoPontos.setText(String.valueOf(categoria.getPontos()));
            }
        });
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

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
        this.tfServicoDescricao.setText(servico.getDescricao());
        this.tfServicoValor.setText(String.valueOf(servico.getValor()));
        cbCategoria.getItems().setAll(ECategoria.values());
        cbCategoria.setValue(servico.getCategoria());
        tfServicoPontos.setText(String.valueOf(servico.getPontos())); // <- garante que mostra os pontos corretamente


        cbCategoria.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                tfServicoPontos.setText(String.valueOf(Servico.getPontosPorCategoria(newVal)));
            }
        });

    }
    

    @FXML
    public void handleBtConfirmar() {
        if (validarEntradaDeDados()) {
            servico.setDescricao(tfServicoDescricao.getText());
            servico.setValor(Double.valueOf(tfServicoValor.getText()));
            servico.getPontosPorCategoria(servico.getCategoria());
            servico.setCategoria(cbCategoria.getValue());

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
        if (this.tfServicoDescricao.getText() == null || this.tfServicoDescricao.getText().length() == 0) {
            errorMessage += "Descrição inválida.\n";
        }

        if (this.tfServicoValor.getText() == null || this.tfServicoValor.getText().length() == 0) {
            errorMessage += "Descrição inválida.\n";
        }

        if (this.tfServicoPontos.getText() == null || this.tfServicoPontos.getText().length() == 0) {
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
