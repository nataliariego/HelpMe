package assembler;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dto.AlumnoDto;

public class AlumnoAssembler {
    public static final String TAG = "ALUMNO_ASSEMBLER";

    public static Map<String, Object> toHashMap(String alumnoStringHash) {
        Map<String, Object> hashMapAlumnoResult = new HashMap<>();

        Log.i(TAG, "INPUT:: " + alumnoStringHash);

        alumnoStringHash = alumnoStringHash.substring(1, alumnoStringHash.length() - 1);
        String[] keyValuePairs = alumnoStringHash.split(",");

        for (String pair : keyValuePairs) {
            String[] entry = pair.split("=");

            if (entry.length == 2) {
                if (entry[1].equalsIgnoreCase("[]")) {
                    hashMapAlumnoResult.put(entry[0], new ArrayList<>());
                } else {
                    hashMapAlumnoResult.put(entry[0], entry[1]);
                }
            } else {
                hashMapAlumnoResult.put(entry[0], null);
            }
        }

        return hashMapAlumnoResult;
    }


    public static AlumnoDto fromHashMapToDto(final HashMap<String, Object> alumnoHashMap) {
        AlumnoDto alumnoResult = new AlumnoDto();

        alumnoResult.nombre = alumnoHashMap.get("nombre") != null ? alumnoHashMap.get("nombre").toString() : "";
        alumnoResult.uo = alumnoHashMap.get("uo") != null ? alumnoHashMap.get("uo").toString() : "";
        alumnoResult.urlFoto = alumnoHashMap.get("url_foto") != null ? alumnoHashMap.get("url_foto").toString() : "";
        //alumnoResult.asignaturasDominadas = ;

        Log.i(TAG, "ALUMNO: " + alumnoResult.nombre + " " + alumnoResult.uo);
        //

        return alumnoResult;
    }

    public static AlumnoDto toDto(final String alumnoHashMap) {
        Map<String, Object> hashMapOrigen = new HashMap<>(toHashMap(alumnoHashMap));

        return fromHashMapToDto(new HashMap<>(hashMapOrigen));
    }
}
