package br.edu.ifsc.fln.model.domain;

public class Modelo {
    private int id;
    private String descricao;

    private Marca marca;

    private Motor motor = new Motor();

    private ECategoria eCategoria;

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Motor getMotor() {
        return motor;
    }

    public void setMotor(Motor motor) {
        this.motor = motor;
    }

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

    public ECategoria geteCategoria() {
        return eCategoria;
    }

    public void seteCategoria(ECategoria eCategoria) {
        this.eCategoria = eCategoria;
    }

    public Modelo() {
    }

    public Modelo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao + " - " + (marca != null ? marca.getNome() : "Sem marca");
    }
}
