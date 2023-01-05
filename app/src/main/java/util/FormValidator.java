package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormValidator {

    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    public static final int UO_LENGTH = 8;

    public static final String TAG = "FORM_VALIDATOR";

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    /**
     * Comprueba si la contraseña indicada cumple con los criterios de seguridad de la aplicación.
     *
     * @param password Contraseña a comprobar.
     * @return true si la contraseña es válida y false en caso contrario.
     */
    public static boolean isPasswordValid(final String password) {
        //Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        //Matcher matcher = pattern.matcher(password);
        //return matcher.matches();
        return password.length() >= 6;
    }

    /**
     * Comprueba si las dos contraseñas indicadas coinciden
     *
     * @param p1 Contraseña
     * @param p2 Contraseña repetidas
     * @return true si coinciden y false en caso contrario.
     */
    public static boolean passwordMatched(final String p1, final String p2) {
        return p1.trim().equals(p2.trim());
    }

    /**
     * Comprueba si el correo electrónico indicado cumple con los criterios especificados por la aplicación.
     *
     * @param email Correo electrónico a comprobar.
     * @return
     */
    public static boolean isEmailValid(final String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public static boolean isValidUOIdentifier(final String uoToCheck) {
        String extractUOLetters = uoToCheck.substring(0, 2);
        String digits = uoToCheck.substring(2);

        return !uoToCheck.isEmpty()
                && extractUOLetters.equalsIgnoreCase("uo")
                && digits.length() == UO_LENGTH - 2;
    }
}
