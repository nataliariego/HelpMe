package com.example.helpme.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Materia{
    /* Campos de la base de datos */
    public static final String COLLECTION = "MATERIA";
    public static final String ABREVIATURA = "abreviatura";
    public static final String DENOMINACION = "denominacion";

    private String id;
    private String denominacion;
    private String abreviatura;

    public Materia(){}
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
