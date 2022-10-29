package com.example.helpme.model;

public class Asignatura {
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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    private String id;
    private String nombre;
    private Curso curso;
    private Materia materia;

    public Asignatura(String id, String nombre, Curso curso, Materia materia) {
        this.id = id;
        this.nombre = nombre;
        this.curso = curso;
        this.materia = materia;
    }
}
