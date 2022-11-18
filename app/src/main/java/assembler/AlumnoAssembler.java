package assembler;

import android.net.Uri;
import android.util.Log;

import com.example.helpme.model.Alumno;
import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import dto.AlumnoDto;

public class AlumnoAssembler {
    public static final String TAG = "ALUMNO_ASSEMBLER";

    public static Map<String, Object> toHashMap(@Nullable Object alumnoStringHash) {
        Map<String, Object> hashMapAlumnoResult = new HashMap<>();

        alumnoStringHash = String.valueOf(alumnoStringHash).substring(1, String.valueOf(alumnoStringHash).length() - 1);
        String[] keyValuePairs = String.valueOf(alumnoStringHash).split(",");

        for (String pair : keyValuePairs) {
            String[] entry = pair.trim().split("=");

            if (entry.length == 2) {
                if (entry[1].trim().equalsIgnoreCase("[]")) {
                    hashMapAlumnoResult.put(entry[0].trim(), new ArrayList<>());
                } else {
                    Log.i(TAG, entry[1]);
                    hashMapAlumnoResult.put(entry[0].trim(), entry[1]);
                }
            } else {
                hashMapAlumnoResult.put(entry[0].trim(), null);
            }
        }

        return hashMapAlumnoResult;
    }

    public static Map<String, Object> toHashMap(AlumnoDto alumno) {
        Map<String, Object> hashMapAlumnoResult = new HashMap<>();

        hashMapAlumnoResult.put(Alumno.NOMBRE, alumno.nombre);
        hashMapAlumnoResult.put(Alumno.UO, alumno.uo);
        hashMapAlumnoResult.put(Alumno.EMAIL, alumno.email);
        hashMapAlumnoResult.put(Alumno.URL_FOTO, alumno.urlFoto);

        return hashMapAlumnoResult;
    }


    public static AlumnoDto fromHashMapToDto(final Map<String, Object> alumnoHashMap) {
        AlumnoDto alumnoResult = new AlumnoDto();

        alumnoResult.nombre = String.valueOf(alumnoHashMap.get(Alumno.NOMBRE));
        alumnoResult.uo = String.valueOf(alumnoHashMap.get(Alumno.UO));
        alumnoResult.urlFoto = String.valueOf(alumnoHashMap.get(Alumno.URL_FOTO));
        alumnoResult.asignaturasDominadas = (Map<String, Object>) alumnoHashMap.get("asignaturasDominadas");

        return alumnoResult;
    }

    public static AlumnoDto toDto(final Object alumnoHashMap) {
        Map<String, Object> hashMapOrigen = toHashMap(alumnoHashMap);
        return fromHashMapToDto(hashMapOrigen);
    }
}
