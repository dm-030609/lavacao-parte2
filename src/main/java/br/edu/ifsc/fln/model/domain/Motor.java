package br.edu.ifsc.fln.model.domain;

public class Motor {

    private int potencia;

    private Modelo modelo;

    private ETipoCombustivel eTipoCombustivel;

    public int getPotencia() {
        return potencia;
    }

    public void setPotencia(int potencia) {
        this.potencia = potencia;
    }

    public ETipoCombustivel geteTipoCombustivel() {
        return eTipoCombustivel;
    }

    public void seteTipoCombustivel(ETipoCombustivel eTipoCombustivel) {
        this.eTipoCombustivel = eTipoCombustivel;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
}
