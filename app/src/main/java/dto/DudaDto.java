package dto;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Materia;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DudaDto {
    public String titulo;
    public String descripcion;
    public Map<String, Object> alumno;
    public String asignatura;
    public Map<String, Object> materia;
    public boolean isResuelta;
    public String fecha;
}
