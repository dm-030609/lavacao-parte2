package br.edu.ifsc.fln.model.domain;

public class Pontuacao {

    private int id;
    private int qtd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public int adicionar(int qtd){
        return this.qtd += qtd;
    }

    public int subtrair(int qtd) {
        if (qtd > this.qtd) {
            throw new RuntimeException("Pontos insuficientes.");
        }
        return this.qtd -= qtd;
    }

    public int saldo() {
        return this.qtd;
    }
}
