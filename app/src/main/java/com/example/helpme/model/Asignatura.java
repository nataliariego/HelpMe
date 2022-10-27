package com.example.helpme.model;

public class Asignatura {

    private String id_curso;
    private String id_materia;
    private String nombre;

    public String getId_curso() {
        return id_curso;
    }

    public void setId_curso(String id_curso) {
        this.id_curso = id_curso;
    }

    public String getId_materia() {
        return id_materia;
    }

    public void setId_materia(String id_materia) {
        this.id_materia = id_materia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Asignatura(String id_curso, String id_materia, String nombre) {
        this.id_curso = id_curso;
        this.id_materia = id_materia;
        this.nombre = nombre;
    }
}
