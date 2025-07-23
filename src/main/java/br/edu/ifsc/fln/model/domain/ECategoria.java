package br.edu.ifsc.fln.model.domain;

public enum ECategoria {
    GRANDE(20),
    PEQUENO(10),
    MEDIO(15),
    MOTO(8),
    PADRAO(5);

    private final int pontos;

    ECategoria(int pontos) {
        this.pontos = pontos;
    }

    public int getPontos() {
        return pontos;
    }
}