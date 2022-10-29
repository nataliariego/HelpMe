package com.example.helpme;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.model.Duda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adapter.DudaAdapter;
import controller.DudaController;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HOME_VIEW";

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;

    private RecyclerView listadoDudasHomeRecycler;

    private DudaAdapter dudaAdapter;

    private DudaController dudaController;

    private List<Duda> dudas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        listadoDudasHomeRecycler.setHasFixedSize(true);
//        listadoDudasHomeRecycler = (RecyclerView) findViewById(R.id.listado_dudas_home);
//        listadoDudasHomeRecycler.setLayoutManager(new LinearLayoutManager(this));




//        dudaAdapter = new DudaAdapter(, new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // TODO: Mostrar pantalla detalle de duda
//            }
//        });
//
//        listadoDudasHomeRecycler.setAdapter(dudaAdapter);

        initCalendarData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        dudaController.findAll().getValue().forEach(d -> {
            Log.i(TAG, d.getTitulo());
        });
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
}