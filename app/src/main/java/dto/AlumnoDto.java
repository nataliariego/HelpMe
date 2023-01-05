package dto;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlumnoDto implements Serializable {

    @Exclude
    public String id;
    public String nombre;
    public String uo;

    @PropertyName("url_foto")
    public String urlFoto;

    public Map<String, Object> asignaturasDominadas;
    public String email;
    @Exclude
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
