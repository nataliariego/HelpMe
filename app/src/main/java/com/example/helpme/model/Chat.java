package com.example.helpme.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String id;
    private Alumno alumnoA;
    private Alumno alumnoB;
    // Guardar aqui ultimo mensaje ?
    private List<Mensaje> mensajes = new ArrayList<>();
}
