/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.ClienteDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.Cliente;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.edu.ifsc.fln.model.domain.PessoaFisica;
import br.edu.ifsc.fln.model.domain.PessoaJuridica;
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

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroClienteController implements Initializable {


    @FXML
    private Button btAlterar;

    @FXML
    private Button btExcluir;

    @FXML
    private Button btInserir;

    @FXML
    private Label lbClienteCelular;

    @FXML
    private Label lbClienteCpfCnpj;

    @FXML
    private Label lbClienteDataCadastro;

    @FXML
    private Label lbClienteDataNascimento;

    @FXML
    private Label lbClienteEmail;

    @FXML
    private Label lbClienteId;

    @FXML
    private Label lbClienteInscricao;

    @FXML
    private Label lbClienteNome;

    @FXML
    private Label lbClienteTipo;

    @FXML
    private TableColumn<Cliente, Integer> tableColumnClienteNome;

    @FXML
    private TableView<Cliente> tableViewClientes;

    
    private List<Cliente> listaClientes;
    private ObservableList<Cliente> observableListClientes;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteDAO.setConnection(connection);
        carregarTableViewCliente();
        
        tableViewClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewClientes(newValue));
    }     
    
    public void carregarTableViewCliente() {
        tableColumnClienteNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        listaClientes = clienteDAO.listar();
        
        observableListClientes = FXCollections.observableArrayList(listaClientes);
        tableViewClientes.setItems(observableListClientes);
    }
    
    public void selecionarItemTableViewClientes(Cliente cliente) {
        if (cliente != null) {
            lbClienteId.setText(String.valueOf(cliente.getId())); 
            lbClienteNome.setText(cliente.getNome());
            lbClienteCelular.setText(cliente.getCelular());
            lbClienteEmail.setText(cliente.getEmail());
            lbClienteDataCadastro.setText(String.valueOf(cliente.getDataCadastro()));

            if(cliente instanceof PessoaFisica pf) {
                lbClienteTipo.setText("Pessoa Física");
                lbClienteCpfCnpj.setText(pf.getCpf());
                lbClienteDataNascimento.setText(String.valueOf(pf.getDataNascimento()));
                lbClienteInscricao.setText("");
            } else if (cliente instanceof PessoaJuridica pj) {
                lbClienteTipo.setText("Pessoa Jurídica");
                lbClienteCpfCnpj.setText(pj.getCnpj());
                lbClienteInscricao.setText(pj.getInscricaoEstadual());
                lbClienteDataNascimento.setText("");
            } else {
                lbClienteTipo.setText("Desconhecido");
                lbClienteCpfCnpj.setText("");
                lbClienteInscricao.setText("");
                lbClienteDataNascimento.setText("");
            }


        } else {
            lbClienteId.setText(""); 
            lbClienteNome.setText("");
            lbClienteCelular.setText("");
            lbClienteEmail.setText("");
            lbClienteDataNascimento.setText("");
        }
        
    }
    
    @FXML
    public void handleBtInserir(ActionEvent event){
        Cliente cliente = getTipoCliente();

        if(cliente != null) {
            cliente.setDataCadastro(new Date());
            try{
                boolean btConfirmarClicked = showFXMLAnchorPaneCadastroClienteDialog(cliente);

                if (btConfirmarClicked) {
                    clienteDAO.inserir(cliente);
                    carregarTableViewCliente();
                    mostrarAlerta("Sucesso", "Cliente inserido com sucesso!");
                }
            }catch (IOException ex) {
                Logger.getLogger(FXMLAnchorPaneCadastroClienteController.class.getName()).log(Level.SEVERE, "Erro ao inserir cliente: " + ex.getMessage(), ex);
                mostrarAlerta("Erro", "Erro ao inserir cliente: " + ex.getMessage());
            }
        }


    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(titulo.equals("Erro") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private Cliente getTipoCliente() {
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Pessoa Física");
        opcoes.add("Pessoa Jurídica");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Pessoa Física", opcoes);
        dialog.setTitle("Dialogo de Opções");
        dialog.setHeaderText("Escolha o tipo de cliente");
        dialog.setContentText("Tipo de cliente: ");
        Optional<String> escolha = dialog.showAndWait();
        return escolha.map(s -> s.equalsIgnoreCase("Pessoa Física") ? new PessoaFisica() : new PessoaJuridica()).orElse(null);
    }
    
    @FXML 
    public void handleBtAlterar() throws IOException {
        Cliente cliente = tableViewClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            boolean btConfirmarClicked = showFXMLAnchorPaneCadastroClienteDialog(cliente);
            if (btConfirmarClicked) {
                clienteDAO.alterar(cliente);
                carregarTableViewCliente();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde um Cliente na tabela ao lado");
            alert.show();
        }
    }
    
    @FXML
    public void handleBtExcluir() throws IOException {
        Cliente cliente = tableViewClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            clienteDAO.remover(cliente);
            carregarTableViewCliente();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde uma Cliente na tabela ao lado");
            alert.show();
        }
    }

    private boolean showFXMLAnchorPaneCadastroClienteDialog(Cliente cliente) throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader();
            AnchorPane page;

            boolean isPF = cliente instanceof PessoaFisica;

            if (isPF) {
                loader.setLocation(getClass().getResource("/view/FXMLAnchorPaneCadastroClientePFDialog.fxml"));

            } else {
                loader.setLocation(getClass().getResource("/view/FXMLAnchorPaneCadastroClientePJDialog.fxml"));

            }

            page = loader.load();


            //criação de um estágio de diálogo (StageDialog)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastro de Cliente");
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);


            //enviando o obejto cliente para o controller
            if(isPF) {
                FXMLAnchorPaneCadastroClientePFDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setCliente((PessoaFisica) cliente);
                //apresenta o diálogo e aguarda a confirmação do usuário
                dialogStage.showAndWait();
                return controller.isBtConfirmarClicked();
            } else {
                FXMLAnchorPaneCadastroClientePJDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setCliente((PessoaJuridica) cliente);
                //apresenta o diálogo e aguarda a confirmação do usuário
                dialogStage.showAndWait();
                return controller.isBtConfirmarClicked();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}

