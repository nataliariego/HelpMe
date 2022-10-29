package com.example.helpme.model;

import java.time.LocalDateTime;

public class RespuestaDuda {
    private String id;
    private String titulo;
    private String descripcion;
    private Duda duda;
    private Alumno alumnoRespuesta;

    private LocalDateTime createdAt;
}
