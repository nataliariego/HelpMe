package com.example.helpme;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.R;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initCalendarData();
    }

    /**
     * Inicializa el contenido del calendario de la pantalla de home.
     */
    private void initCalendarData() {
        txDayOfTheWeek = findViewById(R.id.home_calendar_day_week);
        txDateFormatted = findViewById(R.id.home_calendar_custom_date_today);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txDayOfTheWeek.setText(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es")).toUpperCase());
            txDateFormatted.setText(LocalDate.now().getDayOfMonth() + " "
                    + LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es")).toUpperCase()
                    + " " + LocalDate.now().getYear());
        }



    }
}