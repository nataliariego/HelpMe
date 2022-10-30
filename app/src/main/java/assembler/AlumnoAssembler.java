package assembler;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import dto.AlumnoDto;

public class AlumnoAssembler {
    public static final String TAG = "ALUMNO_ASSEMBLER";

    public static AlumnoDto toDto(String alumnoHash) {

        alumnoHash = alumnoHash.substring(1, alumnoHash.length() - 1);           //remove curly brackets
        String[] keyValuePairs = alumnoHash.split(",");              //split the string to creat key-value pairs
        Map<String, String> map = new HashMap<>();

        for (String pair : keyValuePairs)                        //iterate over the pairs
        {
            Log.i(TAG, "PAIR: " + pair );
            String[] entry = pair.split("=");

            if(entry.length == 2){
                //split the pairs to get key and value
            }

            if (!entry[0].equalsIgnoreCase("asignaturasDominadas")) {
                //Log.i(TAG, entry[0] + " " + entry[1]);
            }

        }

        return null;
    }
}
