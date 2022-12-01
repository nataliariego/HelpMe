package dto;

import com.google.firebase.database.Exclude;

import java.time.LocalDateTime;

public class MensajeDto {
    public String contenido;
    public LocalDateTime fechaEnvio;

    @Exclude
    public String userUid;
}
