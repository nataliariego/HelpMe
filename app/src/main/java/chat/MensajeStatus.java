package chat;

/**
 * Estados de un mensaje del chat.
 *
 * Enviado. Un check, el mensaje ha sido enviado pero no recibido por el destinatario.
 * Recibido. Dos checks (Gris), el mensaje ha sido recibido por el usuario pero éste no lo ha leído.
 * Leído. Dos checks (Azules), el usuario ha leido el mensaje enviado.
 */
public enum MensajeStatus {
    ENVIADO, RECIBIDO, LEIDO
}
