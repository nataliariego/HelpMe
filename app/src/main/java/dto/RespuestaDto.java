package dto;

import java.util.Objects;

public class RespuestaDto {

    public String id;
    public String alumnoDuda;
    public String alumnoResponde;
    public String respuesta;
    public String idDuda;
    public String fecha;
    public String nombreAlumnoResponde;
    public String url_foto_responde;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RespuestaDto that = (RespuestaDto) o;
        return Objects.equals(id, that.id) && Objects.equals(alumnoDuda, that.alumnoDuda) && Objects.equals(alumnoResponde, that.alumnoResponde) && Objects.equals(respuesta, that.respuesta) && Objects.equals(idDuda, that.idDuda) && Objects.equals(fecha, that.fecha) && Objects.equals(nombreAlumnoResponde, that.nombreAlumnoResponde) && Objects.equals(url_foto_responde, that.url_foto_responde);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alumnoDuda, alumnoResponde, respuesta, idDuda, fecha, nombreAlumnoResponde, url_foto_responde);
    }
}
