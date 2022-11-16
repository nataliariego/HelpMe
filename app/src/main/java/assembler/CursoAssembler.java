package assembler;

import com.example.helpme.model.Alumno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dto.AlumnoDto;

public class CursoAssembler {
    public static final String TAG = "CURSO_ASSEMBLER";

    public static Map<String, Object> toHashMap(String cursoStringHash) {
        Map<String, Object> hashMapcursoResult = new HashMap<>();

        System.out.println(cursoStringHash);
        String[] lista = cursoStringHash.split(",");

        String id = lista[0].split("=")[1];
        String[] listaNumero = lista[1].split("=");
        int tamañoDeno = listaNumero[1].length();
        String numero = listaNumero[1].substring(0,tamañoDeno-1);


        hashMapcursoResult.put("id",id);
        hashMapcursoResult.put("numero",numero);
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
        alumnoResult.asignaturasDominadas = Arrays.asList(alumnoHashMap.get("asignaturasDominadas").toString());

        return alumnoResult;
    }

    public static AlumnoDto toDto(final String alumnoHashMap) {
        Map<String, Object> hashMapOrigen = toHashMap(alumnoHashMap);
        return fromHashMapToDto(hashMapOrigen);
    }
}
