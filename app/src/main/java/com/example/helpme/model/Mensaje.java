package com.example.helpme.model;

import java.time.LocalDateTime;

public class Mensaje {

    /* Atributos de la base de datos Realtime - Chat */
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String CREATED_AT = "created_at";
    public static final String CONTENT = "content";
    public static final String CHAT_ID = "chat-id";
    /* ---- */

    private String msg;
    private String idAlumno;
    private String nombreAlumno;
    private String createdAt;

    public Mensaje(String msg, String idAlumno, String nombreAlumno, String createdAt) {
        this.msg = msg;
        this.idAlumno = idAlumno;
        this.nombreAlumno = nombreAlumno;
        this.createdAt = createdAt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
