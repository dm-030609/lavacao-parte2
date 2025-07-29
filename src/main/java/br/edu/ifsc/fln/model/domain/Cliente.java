/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsc.fln.model.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mpisc
 */
public abstract class Cliente extends Object implements ICliente {
    protected int id;
    protected String nome;
    protected String celular;
    protected String email;
    protected Date dataCadastro;

    protected Pontuacao pontuacao = new Pontuacao();

    protected List<Veiculo> veiculos = new ArrayList<>();

    public Cliente() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public Pontuacao getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Pontuacao pontuacao) {
        this.pontuacao = pontuacao;
    }

    public void add(Veiculo veiculo) {
        this.veiculos.add(veiculo);
        veiculo.setCliente(this); // concretização da associação bidirecional
    }

    public void remove(Veiculo veiculo) {
        this.veiculos.remove(veiculo);
        veiculo.setCliente(null);
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override // sobrescrição de método
    public String getDados() {
        StringBuilder dados = new StringBuilder();
        dados.append("\nID...........: ").append(this.id).append("\n");
        dados.append("NOME.........: ").append(this.nome).append("\n");
        dados.append("CELULAR......: ").append(this.celular).append("\n");
        dados.append("EMAIL........: ").append(this.email).append("\n");
        dados.append("DATA CADASTRO: ").append(this.dataCadastro).append("\n");
        dados.append("PONTUAÇÃO....: ").append(getPontuacao().getQtd()).append("\n");
        return dados.toString();
    }

    @Override  // sobrescrição de método
    public String getDados(String msg) {
        StringBuilder dados = new StringBuilder();
        dados.append(getDados());
        dados.append("Menssagem: ").append(msg);
        return dados.toString();
    }
    
}
