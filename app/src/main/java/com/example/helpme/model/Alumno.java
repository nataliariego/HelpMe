package com.example.helpme.model;

public class Alumno {
    private String nombre;
    private String UO;
    private String url_foto;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUO() {
        return UO;
    }

    public void setUO(String UO) {
        this.UO = UO;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public Alumno(String nombre, String UO, String url_foto) {
        this.nombre = nombre;
        this.UO = UO;
        this.url_foto = url_foto;
    }
}
