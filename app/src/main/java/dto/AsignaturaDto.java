package dto;

import com.example.helpme.model.Curso;
import com.example.helpme.model.Materia;

import java.util.Objects;

public class AsignaturaDto {


    public String id;
    public String nombre;
    public String curso;
    public String materia;


    @Override
    public boolean equals(Object o) {
        AsignaturaDto a = (AsignaturaDto) o;
        if (this.nombre.equals(a.nombre)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, curso, materia);
    }

    @Override
    public String toString() {
        return "AsignaturaDto{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", curso='" + curso + '\'' +
                ", materia='" + materia + '\'' +
                '}';
    }
}
