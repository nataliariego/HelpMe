package util;

import android.os.Build;

import com.google.firebase.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Utilidades para trabajar con fechas.
 */
public class DateUtils {

    /**
     * Convierte una fecha en formato TimeStamp de FireStore a LocalDateTime de Java.
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime convertTimeStampToLocalDateTime(Timestamp timestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ZoneId zone = ZoneOffset.systemDefault();
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000), zone);
        }
        return null;
    }
}
