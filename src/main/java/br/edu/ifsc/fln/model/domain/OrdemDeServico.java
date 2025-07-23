package br.edu.ifsc.fln.model.domain;

import java.util.Date;
import java.util.List;

public class OrdemDeServico {
    private int id;
    private long numero;

    private double total;
    private Date agenda;
    private double desconto;

    private List<ItemOS> itemsOS;
    private EStatus eStatus;

    private Veiculo veiculo;

    private Servico servico = new Servico();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
        this.numero = numero;
    }

    public double getTotal() {
        return total;
    }

    public Date getAgenda() {
        return agenda;
    }

    public void setAgenda(Date agenda) {
        this.agenda = agenda;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public EStatus geteStatus() {
        return eStatus;
    }

    public void seteStatus(EStatus eStatus) {
        this.eStatus = eStatus;
    }

    public List<ItemOS> getItemsOS() {
        return itemsOS;
    }

    public void setItemsOS(List<ItemOS> itemsOS) {
        this.itemsOS = itemsOS;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public void calcularServico() {
        total = 0.0;
        for (ItemOS item : this.getItemsOS()) {
            total += item.getValorServico();
        }
        total -= desconto;
    }

    public void add(ItemOS item) {
        this.itemsOS.add(item);
    }

    public void remove(ItemOS item) {
        this.itemsOS.remove(item);
    }
}
