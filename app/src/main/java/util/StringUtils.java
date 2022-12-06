package util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class StringUtils {

    /**
     * Dado un nombre, obtiene las siglas de éste. Por ejemplo: Pedro Martínez --> PM
     * Si el nombre solo es una palabra, se devuelve la sigla de esa palabra y
     * si el nombre está vacío, se retorna una cadena vacía.
     *
     * @param name
     * @return
     */
    public static String getAcronymName(String name) {
        String res = "";

        if (name.isEmpty()) {
            return "";
        }

        String[] words = name.split(" ");
        return words.length == 2 ? getFirstLetterAsUpperCase(words[0])
                .concat(getFirstLetterAsUpperCase(words[1]))
                : getFirstLetterAsUpperCase(words[0]);
    }

    public static String getFirstLetterAsUpperCase(String text) {
        return text.substring(0, 1).toUpperCase();
    }

    /**
     * Convierte el número de bytes pasado como parámetro en un formato más legible
     * para el ser humano, se mostrará en kilobytes no kibibytes y así respectivamente.
     * En función del tamaño indicado, se mostrarán las unidades <code>kb, MB, GB</code>
     *
     * @param bytes Número de bytes.
     * @return
     */
    public static String prettyBytesSize(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;

        // Simbolos para Kilo, Mega y Giga.
        CharacterIterator ci = new StringCharacterIterator("KMG");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%d%cB", (int) value / 1024, ci.current());
    }

    /**
     * Acorta la sentencia pasada como parámetro si excede el número máximo de caracteres indicado.
     * Para ello obtiene la subcadena con el número de caracteres y añade tres puntos al final.
     *
     * @param word          Sentencia de tipo String a acortar.
     * @param maxCharacters Número máximo de caracteres.
     * @return Sentencia acortada si supera el número máximo de caracteres o la sentencia
     * en caso contrario.
     */
    public static String shortWordMaxCharacters(final String word, final int maxCharacters)
            throws IllegalArgumentException {
        if (word == null || maxCharacters < 0) {
            throw new IllegalArgumentException("Los parámetros indicados no son válidos.");
        }
        return word.length() > maxCharacters ? word.trim().substring(0, maxCharacters).concat("...") : word;
    }

    /**
     * Extrae el subtipo al que pertenece el tipo de contenido pasado como parámetro.
     * Por ejemplo, para 'application/pdf' devuelve 'pdf'.
     *
     * @param contentType Tipo de contenido: 'application/pdf', 'iamge/jpeg', etc.
     * @return Tipo de contenido con la información del subtipo mime.
     */
    public static String extractSubtypeFromContentType(final String contentType) {
        if (contentType == null || !contentType.contains("/")) {
            throw new IllegalArgumentException("El tipo mime no es válido.");
        }
        return contentType.split("/")[1];
    }
}
