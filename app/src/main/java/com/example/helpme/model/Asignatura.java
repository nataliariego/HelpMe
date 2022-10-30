package com.example.helpme.model;

public class Asignatura {

    public static final String COLLECTION = "ASIGNATURA";

    public static final String NOMBRE = "nombre";
    public static final String MATERIA = "materia";
    public static final String CURSO = "curso";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    private String id;
    private String nombre;
    private String curso;
    private String materia;

    public Asignatura(String id, String nombre, String curso, String materia) {
        this.id = id;
        this.nombre = nombre;
        this.curso = curso;
        this.materia = materia;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
