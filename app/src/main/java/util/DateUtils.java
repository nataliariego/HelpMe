package util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para trabajar con fechas.
 */
public class DateUtils {

    public static final String DEFAULT_DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

    /**
     * Convierte una fecha en formato TimeStamp de FireStore a LocalDateTime de Java.
     *
     * @param timestamp
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime convertTimeStampToLocalDateTime(Timestamp timestamp) {
        ZoneId zone = ZoneOffset.systemDefault();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000), zone);
    }

    /**
     * Convierte una fecha en formato String a formato LocalDateTime.
     * @param date
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime convertStringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN);
        return LocalDateTime.parse(date, formatter);
    }
}
