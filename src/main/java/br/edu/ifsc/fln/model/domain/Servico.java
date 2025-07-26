package br.edu.ifsc.fln.model.domain;

import java.util.HashMap;
import java.util.Map;

public class Servico {
    private int id;
    private String descricao;
    private double valor;

    private ECategoria categoria;
    private static final Map<ECategoria, Integer> pontosPorCategoria = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    static {
        pontosPorCategoria.put(ECategoria.MOTO, 8);
        pontosPorCategoria.put(ECategoria.PEQUENO, 10);
        pontosPorCategoria.put(ECategoria.MEDIO, 15);
        pontosPorCategoria.put(ECategoria.GRANDE, 20);
        pontosPorCategoria.put(ECategoria.PADRAO, 5);
    }

    public static int getPontosPorCategoria(ECategoria categoria) {
        return pontosPorCategoria.getOrDefault(categoria, 0);
    }



    public ECategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(ECategoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public int getPontos() {
        return getPontosPorCategoria(this.categoria);
    }

}
