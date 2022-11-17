package dto;

import java.util.ArrayList;
import java.util.List;

public class AlumnoDto {
    public String id;
    public String nombre;
    public String uo;
    public String urlFoto;
    public List<String> asignaturasDominadas = new ArrayList<>();
    public String email;
    public String password;

    @Override
    public String toString() {
        return "AlumnoDto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", uo='" + uo + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", asignaturasDominadas=" + asignaturasDominadas +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
