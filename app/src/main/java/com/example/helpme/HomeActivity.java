package com.example.helpme;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Duda;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import adapter.DudaAdapter;
import controller.DudaController;
import viewmodel.DudaViewModel;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HOME_ACTIVITY";

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;

    private RecyclerView listaDudasRecycler;

    private RecyclerView listadoDudasHomeRecycler;

    private DudaAdapter dudaAdapter;

    private DudaController dudaController;

    private DudaViewModel dudaViewModel = new DudaViewModel();

    private List<Duda> dudas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listaDudasRecycler = findViewById(R.id.listado_dudas_home_recycler);

        //dudaViewModel = ViewModelProviders.of(this).get(DudaViewModel.class);

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
//        listadoDudasHomeRecycler.setAdapter(dudaAdapter)

        initCalendarData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        dudaViewModel.getAllDudas().observe(this, dudas -> {
            this.dudas = dudas;

            Log.i(TAG, "pasando por el observer");

            if(dudas != null){
                dudas.forEach(d -> {
                    Log.i(TAG, d.getTitulo() + " " + d.getAlumnoId());
                });

                listaDudasRecycler.setAdapter(new ListaDudasAdapter(dudas));

            }

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