package br.edu.ifsc.fln.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class FXMLVBoxMainAppController implements Initializable {

    @FXML
    private MenuItem menuItemCadastroCor;
    @FXML
    private MenuItem menuItemCadastroMarca;
    @FXML
    private MenuItem menuItemCadastroModelo;
    @FXML
    private MenuItem menuItemCadastroVeiculo;
    @FXML
    private MenuItem menuItemCadastroCliente;
    @FXML
    private MenuItem menuItemCadastroServico;
    @FXML
    private MenuItem menuItemProcessoOrdemServico;
    @FXML
    private MenuItem menuItemProcessoEstoque;
    @FXML
    private MenuItem menuItemGraficoOrdemServicoPorMes;
    @FXML
    private MenuItem menuItemGraficoVendasPorMes;

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialização, se necessário
    }

    @FXML
    public void handleMenuItemCadastroCor() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroCor.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemCadastroMarca() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroMarca.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemCadastroModelo() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroModelo.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemCadastroVeiculo() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroVeiculo.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemCadastroCliente() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroCliente.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemCadastroServico() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroServico.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemProcessoOrdemServico() throws IOException {
        AnchorPane a = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneCadastroOrdemServico.fxml"));
        anchorPane.getChildren().setAll(a);
    }

    @FXML
    public void handleMenuItemGraficosOrdemServicoPorMes(ActionEvent event) throws IOException {
        Parent node = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneGraficoOS.fxml"));
        anchorPane.getChildren().setAll(node);
    }



    @FXML
    public void handleMenuItemGraficosVendasPorMes(ActionEvent event) throws IOException {
        Parent node = FXMLLoader.load(getClass().getResource("/view/FXMLAnchorPaneGraficos.fxml"));
        anchorPane.getChildren().setAll(node);
    }


    @FXML
    public void handleMenuItemRelatorioEstoqueProdutos() throws IOException {
        // TODO: implementar
    }
}
