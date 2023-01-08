package util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utilidades para trabajar con fechas.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class DateUtils {

    public static final String TAG = "DATE_UTILS";

    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ALT_FORMAT_DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Madrid");
    public static final Locale DEFAULT_LOCALE = new Locale("ES", "es");
    private static PrettyTime prettyTime = new PrettyTime();

    /**
     * Convierte una fecha en formato TimeStamp de FireStore a LocalDateTime de Java.
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime convertTimeStampToLocalDateTime(Timestamp timestamp) {
        ZoneId zone = ZoneOffset.systemDefault();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000), zone);
    }

    /**
     * Convierte una fecha en formato hashmap de firebase a LocaldateTime.
     *
     * @param date
     * @return
     */
    public static LocalDateTime convertHashMapToLocalDateTime(Map<String, Object> date) {
        if (date == null) {
            return null;
        }

        Map<String, Object> chronology = ((HashMap<String, Object>) date.get("chronology"));

        int dayOfMonth = ((Long) date.get("dayOfMonth")).intValue();
        String dayOfWeek = String.valueOf(date.get("dayOfWeek"));
        int dayOfYear = ((Long) date.get("dayOfYear")).intValue();
        int hour = ((Long) date.get("hour")).intValue();
        int minute = ((Long) date.get("minute")).intValue();
        String month = String.valueOf(date.get("month"));
        int monthValue = ((Long) date.get("monthValue")).intValue();
        int nano = ((Long) date.get("nano")).intValue();
        int second = ((Long) date.get("second")).intValue();
        int year = ((Long) date.get("year")).intValue();

        return LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second, nano);
    }

    /**
     * Convierte una fecha en formato String a formato LocalDateTime.
     *
     * @param date
     * @param format 0 para formatear en {@link #DEFAULT_DATETIME_PATTERN} y 1
     *               en {@link #ALT_FORMAT_DATETIME_PATTERN}
     * @return
     */
    public static LocalDateTime convertStringToLocalDateTime(String date, int format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format == 0
                ? DEFAULT_DATETIME_PATTERN : ALT_FORMAT_DATETIME_PATTERN);
        return LocalDateTime.parse(date, formatter);
    }

    public static String prettyDate(final String dateTimeInStampFormat, int format) {
        prettyTime.setLocale(new Locale("es"));
        String resPrettyDate = prettyTime.format(convertStringToLocalDateTime(dateTimeInStampFormat, format), DEFAULT_ZONE_ID);
        return resPrettyDate.substring(0, 1).toUpperCase()
                .concat(resPrettyDate.substring(1));
    }

    public static String prettyDate(final LocalDateTime localDateTime) {
        prettyTime.setLocale(new Locale("es"));
        String resPrettyDate = prettyTime.format(localDateTime, DEFAULT_ZONE_ID);
        return resPrettyDate.substring(0, 1).toUpperCase()
                .concat(resPrettyDate.substring(1));
    }

    /**
     * Hora en formato HH:mm
     *
     * @param date Fecha de tipo LocalDateTime
     * @return Ejemplo: 2022-12-02 12:14:57.670 --> 12:14
     */
    public static String getSimplifiedDate(final String date) {
        if (date == null) {
            return "no-date";
        }

        LocalDateTime dateTime = convertStringToLocalDateTime(date, 0);

        return String.valueOf(dateTime.getHour())
                .concat(":")
                .concat(String.valueOf(dateTime.getMinute()));
    }

    /**
     * @return Hora actual con el formato establecido para la aplicación.
     */
    public static String getNowWithPredefinedFormat() {
        Instant now = Instant.now();
        LocalDateTime nowDateTime = LocalDateTime.ofInstant(now, DEFAULT_ZONE_ID);

        return String.valueOf(nowDateTime
                .format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN, DEFAULT_LOCALE)));
    }

    public static String format(final String date) {
        String part1 = date.split(" ")[0]; // dd/MM/YYYY
        String part2 = date.split(" ")[1]; //HH:mm:ss

        int day = Integer.parseInt(part1.split("/")[0]);
        int month = Integer.parseInt(part1.split("/")[1]);
        String year = part1.split("/")[2];

        int hour = Integer.parseInt(part2.split(":")[0]);
        int minute = Integer.parseInt(part2.split(":")[1]);
        int second = Integer.parseInt(part2.split(":")[2]);

        return String.format("%02d/%02d/%s %02d:%02d:%02d", day, month, year, hour, minute, second);
    }
}
