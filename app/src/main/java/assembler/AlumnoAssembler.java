package assembler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import dto.AlumnoDto;

public class AlumnoAssembler {
    public static final String TAG = "ALUMNO_ASSEMBLER";

    public static Map<String, Object> toHashMap(String alumnoStringHash) {
        Map<String, Object> hashMapAlumnoResult = new HashMap<>();

        alumnoStringHash = alumnoStringHash.substring(1, alumnoStringHash.length() - 1);
        String[] keyValuePairs = alumnoStringHash.split(",");

        for (String pair : keyValuePairs) {
            String[] entry = pair.trim().split("=");

            if (entry.length == 2) {
                if (entry[1].trim().equalsIgnoreCase("[]")) {
                    hashMapAlumnoResult.put(entry[0].trim(), new ArrayList<>());
                } else {
                    hashMapAlumnoResult.put(entry[0].trim(), entry[1]);
                }
            } else {
                hashMapAlumnoResult.put(entry[0].trim(), null);
            }
        }

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
