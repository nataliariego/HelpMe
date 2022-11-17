package assembler;

import com.example.helpme.model.Alumno;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dto.AlumnoDto;

public class MateriaAssembler {
    public static final String TAG = "MATERIA_ASSEMBLER";

    public static Map<String, Object> toHashMap(String materiStringHash) {
        Map<String, Object> hashMapcursoResult = new HashMap<>();

        System.out.println(materiStringHash);
        String[] lista = materiStringHash.split(",");

        String denominacion = lista[0].split("=")[1];

        String abreviatura = lista[1].split("=")[1];

        String[] listaId = lista[2].split("=");
        int tamañoId = listaId[1].length();
        String id = listaId[1].substring(0,tamañoId-1);

        hashMapcursoResult.put("denominacion",denominacion);
        hashMapcursoResult.put("abreviatura",abreviatura);
        hashMapcursoResult.put("id",id);


        return hashMapcursoResult;
    }

    public static Map<String, Object> toHashMap(AlumnoDto alumno) {
        Map<String, Object> hashMapAlumnoResult = new HashMap<>();

        hashMapAlumnoResult.put(Alumno.NOMBRE, alumno.nombre);
        hashMapAlumnoResult.put(Alumno.UO, alumno.uo);
        hashMapAlumnoResult.put(Alumno.EMAIL, alumno.email);

        return hashMapAlumnoResult;
    }


    public static AlumnoDto fromHashMapToDto(final Map<String, Object> alumnoHashMap) {
        AlumnoDto alumnoResult = new AlumnoDto();

        alumnoResult.nombre = String.valueOf(alumnoHashMap.get("nombre"));
        alumnoResult.uo = String.valueOf(alumnoHashMap.get("uo"));
        alumnoResult.urlFoto = String.valueOf(alumnoHashMap.get("url_foto"));
        alumnoResult.asignaturasDominadas = (Map<String, Object>) alumnoHashMap.get("asignaturasDominadas");

        return alumnoResult;
    }

    public static AlumnoDto toDto(final String alumnoHashMap) {
        Map<String, Object> hashMapOrigen = toHashMap(alumnoHashMap);
        return fromHashMapToDto(hashMapOrigen);
    }
}
