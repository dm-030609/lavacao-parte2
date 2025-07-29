package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.PontuacaoDAO;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
    @FXML private Button buttonRemoverServico;

    @FXML private TableView<ItemOS> tableViewItens;
    @FXML private TableColumn<ItemOS, String> colServico;
    @FXML private TableColumn<ItemOS, String> colValor;
    @FXML private TableColumn<ItemOS, String> colObservacoes;

    @FXML private TextField textFieldDesconto;
    @FXML private TextField textFieldValorTotal;
    @FXML private ComboBox<String> comboBoxStatus;
    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;
    @FXML private Button btnResgatarFidelidade;

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

        comboBoxStatus.setItems(FXCollections.observableArrayList("ABERTA", "FECHADA", "CANCELADA"));

        colServico.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getServico().getDescricao()));
        colValor.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getValorServico())));
        colObservacoes.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getObservacoes()));

        tableViewItens.setItems(listaItens);

        comboBoxPlaca.setOnAction(e -> preencherCamposVeiculo());
        comboBoxServico.setOnAction(e -> preencherValorServico());

        textFieldDesconto.textProperty().addListener((observable, oldValue, newValue) -> calcularValorComDesconto());
    }

    public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
        this.ordemDeServico = ordemDeServico;

        if (ordemDeServico != null) {
            comboBoxPlaca.setValue(ordemDeServico.getVeiculo());
            preencherCamposVeiculo();

            if (ordemDeServico.getId() > 0) {
                comboBoxPlaca.setDisable(true);
                comboBoxPlaca.setStyle("-fx-opacity: 1.0; -fx-background-color: #E0E0E0;");
                textFieldCliente.setEditable(false);
                textFieldCliente.setStyle("-fx-background-color: #E0E0E0;");
                textFieldModelo.setEditable(false);
                textFieldModelo.setStyle("-fx-background-color: #E0E0E0;");
                textFieldMarca.setEditable(false);
                textFieldMarca.setStyle("-fx-background-color: #E0E0E0;");
                textFieldCategoria.setEditable(false);
                textFieldCategoria.setStyle("-fx-background-color: #E0E0E0;");
            }

            if (ordemDeServico.getAgenda() != null) {
                java.sql.Date dataSql = new java.sql.Date(ordemDeServico.getAgenda().getTime());
                datePickerData.setValue(dataSql.toLocalDate());
            }

            if (ordemDeServico.geteStatus() != null) {
                comboBoxStatus.setValue(ordemDeServico.geteStatus().name());
            } else {
                comboBoxStatus.setValue(EStatus.ABERTA.name());
            }

            textFieldDesconto.setText(String.format("%.2f", ordemDeServico.getDesconto()));
            textFieldValorTotal.setText(String.format("%.2f", ordemDeServico.getTotal()));

            listaItens.clear();
            listaItens.addAll(ordemDeServico.getItemsOS());
            tableViewItens.refresh();
        }
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
            Cliente cliente = veiculo.getCliente();
            textFieldCliente.setText(cliente.getNome());

            // 👇 FORÇA carregar a pontuação
            PontuacaoDAO pontuacaoDAO = new PontuacaoDAO(connection);
            cliente.setPontuacao(pontuacaoDAO.buscarPorClienteId(cliente.getId()));

            Modelo modelo = veiculo.getModelo();
            if (modelo != null) {
                textFieldModelo.setText(modelo.getDescricao());

                Marca marca = modelo.getMarca();
                textFieldMarca.setText(marca != null ? marca.getNome() : "Marca não encontrada");

                textFieldCategoria.setText(modelo.geteCategoria() != null ?
                        modelo.geteCategoria().name() : "Categoria indefinida");
            } else {
                textFieldModelo.setText("Modelo não encontrado");
                textFieldMarca.setText("");
                textFieldCategoria.setText("");
            }
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
        Servico servicoSelecionado = comboBoxServico.getSelectionModel().getSelectedItem();
        String valorTexto = textFieldValorServico.getText().replace(",", ".");
        String observacoes = textFieldObservacoes.getText();

        if (servicoSelecionado == null || valorTexto.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Campos obrigatórios");
            alert.setContentText("Selecione um serviço e insira o valor.");
            alert.showAndWait();
            return;
        }

        for (ItemOS itemExistente : listaItens) {
            if (itemExistente.getServico().getId() == servicoSelecionado.getId()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING);
                alerta.setTitle("Serviço Duplicado");
                alerta.setHeaderText("Serviço já adicionado");
                alerta.setContentText("Este serviço já foi adicionado à ordem de serviço.");
                alerta.showAndWait();
                return;
            }
        }

        try {
            double valor = Double.parseDouble(valorTexto);

            ItemOS item = new ItemOS();
            item.setServico(servicoSelecionado);
            item.setValorServico(valor);
            item.setObservacoes(observacoes);

            ordemDeServico.getItemsOS().add(item);
            listaItens.add(item);

            atualizarValorTotal();

            comboBoxServico.getSelectionModel().clearSelection();
            textFieldValorServico.clear();
            textFieldObservacoes.clear();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Valor inválido");
            alert.setContentText("O valor inserido não é um número válido.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleButtonRemoverServico() {
        ItemOS itemSelecionado = tableViewItens.getSelectionModel().getSelectedItem();
        if (itemSelecionado != null) {
            listaItens.remove(itemSelecionado);
            ordemDeServico.getItemsOS().remove(itemSelecionado);
            atualizarValorTotal();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Nenhum item selecionado");
            alert.setContentText("Por favor, selecione um item da tabela para remover.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleButtonConfirmar() {
        if (ordemDeServico != null) {
            ordemDeServico.setVeiculo(comboBoxPlaca.getSelectionModel().getSelectedItem());

            if (ordemDeServico.getVeiculo() != null) {
                ordemDeServico.getVeiculo().setCliente(ordemDeServico.getVeiculo().getCliente());
            }

            if (datePickerData.getValue() != null) {
                ordemDeServico.setAgenda(java.sql.Date.valueOf(datePickerData.getValue()));
            } else {
                ordemDeServico.setAgenda(new java.sql.Date(System.currentTimeMillis()));
            }

            ordemDeServico.seteStatus(EStatus.valueOf(comboBoxStatus.getValue()));

            try {
                double desconto = Double.parseDouble(textFieldDesconto.getText().replace(",", "."));
                ordemDeServico.setDesconto(desconto);
            } catch (NumberFormatException e) {
                ordemDeServico.setDesconto(0.0);
            }

            ordemDeServico.setItemsOS(new ArrayList<>(listaItens));

            ordemDeServico.calcularServico();

            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleButtonCancelar() {
        dialogStage.close();
    }


    @FXML
    private void handleResgatarFidelidade() {
        if (ordemDeServico != null && ordemDeServico.getVeiculo() != null) {
            Cliente cliente = ordemDeServico.getVeiculo().getCliente();


            if (cliente != null && cliente.getPontuacao() != null) {
                int pontosAtuais = cliente.getPontuacao().getQtd();

                System.out.println(">>> PONTOS ATUAIS: " + pontosAtuais);

                if (pontosAtuais >= 100) {
                    cliente.getPontuacao().setQtd(pontosAtuais - 100);
                    new PontuacaoDAO(connection).atualizar(cliente.getPontuacao(), cliente.getId());

                    // Desconta R$ 100 do valor total
                    double total = Double.parseDouble(textFieldValorTotal.getText().replace(",", "."));
                    double valorComDesconto = Math.max(0, total - 100); // evita negativo

                    textFieldValorTotal.setText(String.format("%.2f", valorComDesconto).replace(".", ","));

                    calcularValorComDesconto();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Resgate de Fidelidade");
                    alert.setHeaderText(null);
                    alert.setContentText("R$ 100,00 de desconto aplicados com sucesso!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Pontuação insuficiente");
                    alert.setHeaderText(null);
                    alert.setContentText("Você precisa de no mínimo 100 pontos para resgatar a fidelidade.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Não foi possível identificar a pontuação do cliente.");
                alert.showAndWait();
            }
        }
    }


    private void calcularValorComDesconto() {
        double total = tableViewItens.getItems().stream()
                .mapToDouble(ItemOS::getValorServico)
                .sum();

        try {
            double desconto = Double.parseDouble(textFieldDesconto.getText());
            double valorFinal = total - (total * (desconto / 100));
            textFieldValorTotal.setText(String.format("%.2f", valorFinal));
        } catch (NumberFormatException e) {
            textFieldValorTotal.setText(String.format("%.2f", total));
        }
    }

    private void atualizarValorTotal() {
        double total = 0.0;
        for (ItemOS item : listaItens) {
            total += item.getValorServico();
        }
        textFieldValorTotal.setText(String.format("%.2f", total).replace(".", ","));
    }
}
