package com.example.helpme.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static final String REFERENCE = "chats";
    public static final String ALUMNO_A = "alumnoA";
    public static final String ALUMNO_B = "alumnoB";

    private String id;
    private Alumno alumnoA;
    private Alumno alumnoB;
    // Guardar aqui ultimo mensaje ?
    private List<Mensaje> mensajes = new ArrayList<>();

    public Chat(String id, Alumno alumnoA, Alumno alumnoB) {
        this.id = id;
        this.alumnoA = alumnoA;
        this.alumnoB = alumnoB;
    }

    public Alumno getAlumnoA() {
        return alumnoA;
    }

    public void setAlumnoA(Alumno alumnoA) {
        this.alumnoA = alumnoA;
    }

    public Alumno getAlumnoB() {
        return alumnoB;
    }

    public void setAlumnoB(Alumno alumnoB) {
        this.alumnoB = alumnoB;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }
}
