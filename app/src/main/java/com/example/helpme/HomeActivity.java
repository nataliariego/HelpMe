package com.example.helpme;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Duda;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapter.DudaAdapter;
import controller.DudaController;
import dto.DudaDto;
import viewmodel.DudaViewModel;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HOME_ACTIVITY";

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;

    private RecyclerView listadoDudasHomeRecycler;

    private DudaAdapter dudaAdapter;

    private DudaController dudaController;

    private DudaViewModel dudaViewModel = new DudaViewModel();

    private List<DudaDto> dudas = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initCalendarData();

        listadoDudasHomeRecycler = (RecyclerView) findViewById(R.id.listado_dudas_home_recycler);
        listadoDudasHomeRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listadoDudasHomeRecycler.setLayoutManager(layoutManager);

        cargarDudas();

        dudaAdapter = new DudaAdapter(dudas);
        listadoDudasHomeRecycler.setAdapter(dudaAdapter);

        dudaAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        cargarDudas();

        dudaAdapter = new DudaAdapter(dudas);
        listadoDudasHomeRecycler.setAdapter(dudaAdapter);

        dudaAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarDudas() {
        dudas.clear();

        dudaViewModel.getAllDudas().observe(this, dudasResult -> {
            //this.dudas = dudas;

            Log.i(TAG, "pasando por el observer" + dudasResult.get(0).getAlumnoId() + " " + dudasResult.get(0).getAsignaturaId());

            if (dudasResult != null) {
                dudasResult.forEach(d -> {
                    Log.i(TAG, d.getTitulo() + " " + d.getAlumnoId());
                    DudaDto newDuda = new DudaDto();
                    newDuda.titulo = d.getTitulo();
                    newDuda.alumno = d.getAlumnoId();
                    newDuda.asignatura = d.getAsignaturaId();
                    newDuda.fecha = d.getFecha();

                    dudas.add(newDuda);
                });
            }
        });
    }

    private void printDudas() {

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

    private void cargarDudasInit() {
        Duda d1 = new Duda("Duda 1", "asdfasfd", "asdfasdf", "000", "999", false, "20/10/2022 12:00:01");
//        dudas.add(d1);
    }
}