package util;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeUtils {

    public static final double MAX_DOCUMENT_SIZE = 1024 * 1; // 1MB
    public static final double MAX_IMAGE_SIZE = 1024 * 1; // 1MB

    public static final String PDF_TYPE = "pdf";

    /* Microsoft Office */
    public static final String WORD_TYPE = "word";
    public static final String EXCEL_TYPE = "xsl";
    public static final String TEXT_TYPE = "txt";

    /* Audio */
    public static final String MP3_TYPE = "mp3";

    /* Imagen */
    public static final String JPEG_TYPE = "jpg";

    public static Map<String, String> availableTypes = new HashMap<>();

    /**
     * Establecimiento de los tipos de contenido soportados en la aplicación.
     */
    private static void setAvailableTypes() {
        availableTypes.clear();

        /* Documentos */
        availableTypes.put("application/pdf", PDF_TYPE);
        availableTypes.put("application/msword", WORD_TYPE);
        availableTypes.put("application/vnd.ms-excel", EXCEL_TYPE);
        availableTypes.put("text/plain", TEXT_TYPE);

        /* Imágenes */
        availableTypes.put("imgage/jpeg", JPEG_TYPE);

        /* Audios */
        availableTypes.put("audio/mpeg", MP3_TYPE);
    }

    public static Map<String, String> getAvailableTypes() {
        if (availableTypes.size() == 0) {
            setAvailableTypes();
        }
        return availableTypes;
    }

    public static boolean isValidFile(final Uri uri){
        return isValidFile(uri);
    }

    public static boolean validSize(final Uri uri){
        return uri.getPath().getBytes().length < MAX_DOCUMENT_SIZE;
    }
}
