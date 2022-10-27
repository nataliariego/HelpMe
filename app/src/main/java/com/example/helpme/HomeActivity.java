package com.example.helpme;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initCalendarData();

        readData();
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
                    + LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, new Locale("es")).replace(".", "").toUpperCase()
                    + " " + LocalDate.now().getYear());
        }
    }

    private void readData() {
        db.collection("DUDA")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}