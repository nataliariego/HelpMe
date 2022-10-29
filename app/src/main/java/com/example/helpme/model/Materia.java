package com.example.helpme.model;

public class Materia {
    private String id;
    private String denominacion;
    private String abreviatura;

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public Materia(String id, String denominacion, String abreviatura) {
        this.id = id;
        this.denominacion = denominacion;
        this.abreviatura = abreviatura;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "id='" + id + '\'' +
                ", denominacion='" + denominacion + '\'' +
                ", abreviatura='" + abreviatura + '\'' +
                '}';
    }
}
