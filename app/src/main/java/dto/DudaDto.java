package dto;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Materia;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DudaDto {
    public String titulo;
    public String descripcion;
    public Map<String, Object> alumno;
    public String asignatura;
    public Map<String, Object> materia;
    public boolean isResuelta;
    public String fecha;
    public String id;
    public String url_adnjuto;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DudaDto dudaDto = (DudaDto) o;
        return isResuelta == dudaDto.isResuelta && Objects.equals(titulo, dudaDto.titulo) && Objects.equals(descripcion, dudaDto.descripcion) && Objects.equals(alumno, dudaDto.alumno) && Objects.equals(asignatura, dudaDto.asignatura) && Objects.equals(materia, dudaDto.materia) && Objects.equals(fecha, dudaDto.fecha) && Objects.equals(id, dudaDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, descripcion, alumno, asignatura, materia, isResuelta, fecha, id);
    }
}
