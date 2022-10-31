package util;

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
}
