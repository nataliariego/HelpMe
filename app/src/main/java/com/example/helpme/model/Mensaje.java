package com.example.helpme.model;

public class Mensaje {

    /* Atributos de la base de datos Realtime - Chat */
    public static final String REFERENCE = "messages";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String CREATED_AT = "created_at";
    public static final String CONTENT = "content";
    public static final String CHAT_ID = "chat-id";
    public static final String RESOURCE_URL = "resource_url";
    public static final String MESSAGE_TYPE = "type";

    /* Metadatos para archivos */
    public static final String FILE_SIZE = "size";
    public static final String FILE_PAGES = "pages";
    public static final String FILE_DURATION = "size";
    public static final String FILE_PRETTY_TYPE = "pretty_type";
    public static final String FILE_NAME = "name";

    /* ---- */

    public static final String DEFAULT_TYPE = "text";

    private String msg;
    private String idAlumno;
    private String nombreAlumno;
    private String tipo; /* text, image, pdf... --> Diferencia MIME Type */
    private String resourceUrl;
    private String createdAt;

    public Mensaje(String msg, String idAlumno, String nombreAlumno, String createdAt) {
        this(msg, idAlumno, nombreAlumno, DEFAULT_TYPE, createdAt);
    }

    public Mensaje(String msg, String idAlumno, String nombreAlumno, String tipo, String createdAt) {
        this.msg = msg;
        this.idAlumno = idAlumno;
        this.nombreAlumno = nombreAlumno;
        this.createdAt = createdAt;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
