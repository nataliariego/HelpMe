package dto;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.time.LocalDateTime;

import chat.MensajeStatus;

public class MensajeDto {
    public String contenido;

    @Exclude
    public LocalDateTime fechaEnvio;

    @PropertyName("created_at")
    public String createdAt;

    @Exclude
    public String userUid;

    @PropertyName("type")
    public String mimeType;

    public MensajeStatus status;

    public String prettyType; // Ej: word, pdf, etc.
    public String filename;
    public String prettySize; // Ej. 14KB, 1MB, etc.

    /* Si el usuario se ha dado de baja en la aplicación */
    public String deleted;

    @PropertyName("deleted_at")
    public String deletedAt;

}
