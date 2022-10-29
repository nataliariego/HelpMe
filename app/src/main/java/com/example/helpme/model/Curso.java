package com.example.helpme.model;

public class Curso {
    private String id;
    private String numero;

    public Curso(String id, String numero) {
        this.id = id;
        this.numero = numero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id='" + id + '\'' +
                ", numero='" + numero + '\'' +
                '}';
    }
}
