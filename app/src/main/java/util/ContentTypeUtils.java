package util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContentTypeUtils {

    public static final String TAG = "CONTENT_TYPE_UTILS";

    public static final String PDF_TYPE = "pdf";

    /* Microsoft Office */
    public static final String WORD_TYPE = "word";
    public static final String EXCEL_TYPE = "xsl";
    public static final String TEXT_TYPE = "txt";

    /* Audio */
    public static final String MP3_TYPE = "mp3";

    /* Imagen */
    public static final String JPEG_TYPE = "jpg";

    /* Tamaño máximo de subida de archivos = ~1MB */
    public static final int MAX_FILE_SIZE_TO_UPLOAD = 1000000;

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

    /**
     * Comprueba que el tamaño de la imagen es inferior a {@link #MAX_FILE_SIZE_TO_UPLOAD}
     *
     * @return true si es válido y false en caso contrario.
     */
    public static boolean isImageSizeValid(final ImageView imageView) {
        return imageView != null && Objects.requireNonNull(getImageBytes(imageView)).length < MAX_FILE_SIZE_TO_UPLOAD;
    }

    /**
     * @return Número de bytes de una imagen pasada como parámtro.
     */
    public static byte[] getImageBytes(final ImageView imageView) {
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytesData = stream.toByteArray();
            stream.close();
            return bytesData;
        } catch (Exception e) {
            Log.e(TAG, "Error al comprobar el tamaño del archivo. " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
