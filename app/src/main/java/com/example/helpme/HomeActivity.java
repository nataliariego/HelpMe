package com.example.helpme;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Duda;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapter.DudaAdapter;
import auth.Authentication;
import dto.DudaDto;
import network.NetworkStatusChecker;
import network.NetworkStatusHandler;
import viewmodel.DudaViewModel;

public class HomeActivity extends AppCompatActivity implements NetworkStatusHandler {

    public static final String TAG = "HOME_ACTIVITY";
    public static final String DUDA_SELECCIONADA = "duda_seleccionada";

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;
    private Button btVerTodasDudas;
    private RecyclerView listadoDudasHomeRecycler;
    private Button btNuevaDuda;

    private DudaAdapter dudaAdapter;
    private DudaViewModel dudaViewModel = new DudaViewModel();

    private List<DudaDto> dudas = new ArrayList<>();

    private BottomNavigationView navegacion;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initCalendarData();

        if (!Authentication.getInstance().isSigned()) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        btVerTodasDudas = (Button) findViewById(R.id.btVerTodasDudas);
        btNuevaDuda = (Button) findViewById(R.id.bt_nueva_duda_home); // Es un layout no un boton
        listadoDudasHomeRecycler = (RecyclerView) findViewById(R.id.listado_dudas_home_recycler);

        listadoDudasHomeRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listadoDudasHomeRecycler.setLayoutManager(layoutManager);

        cargarDudas();

        //Navegación
        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());

        /* Redirecciones a otras pantallas */

        btVerTodasDudas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPantallaListadoDudas();
            }
        });

        btNuevaDuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPantallaPublicarNuevaDuda();
            }
        });
    }

    /**
     * Redirecciona al Activity ListarDudasActivity.
     */
    private void redirectPantallaListadoDudas() {
        Intent listadoDudasIntent = new Intent(HomeActivity.this, ListarDudasActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent);
    }

    /**
     * Redirecciona al Activity PublicarDudasActivity.
     */
    private void redirectPantallaPublicarNuevaDuda() {
        Intent publicarDudasIntent = new Intent(HomeActivity.this, PublicarDudaActivity.class);
        startActivity(publicarDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarDudas() {
        dudas.clear();

        dudaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                dudasResult.forEach(d -> {
                    DudaDto newDuda = new DudaDto();
                    newDuda.id = d.getId();
                    newDuda.titulo = d.getTitulo();
                    newDuda.descripcion = d.getDescripcion();
                    newDuda.alumno = d.getAlumnoId();
                    newDuda.asignatura = d.getAsignaturaId();
                    newDuda.materia = d.getMateriaId();
                    newDuda.fecha = d.getFecha();
                    newDuda.url_adnjuto = d.getUrl_adjunto();

                    dudas.add(newDuda);
                });
            }

            dudaAdapter = new DudaAdapter(dudas,
                    new DudaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DudaDto duda) {
                            clikonIntem(duda);
                        }
                    });
            listadoDudasHomeRecycler.setAdapter(dudaAdapter);

            dudaAdapter.notifyDataSetChanged();
        });
    }

    public void clikonIntem(DudaDto duda) {
        Duda dudaCreada = crearDuda(duda);
        Intent intent = new Intent(HomeActivity.this, ResolveActivity.class);
        intent.putExtra(DUDA_SELECCIONADA, dudaCreada);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private Duda crearDuda(DudaDto duda) {
        Duda d = new Duda(duda.titulo, duda.descripcion, duda.alumno, duda.asignatura, duda.materia, duda.isResuelta, duda.fecha, duda.id, duda.url_adnjuto);
        return d;
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

    @Override
    public void checkConnection() {
        NetworkStatusChecker.getInstance().handleConnection(getApplicationContext(), new NetworkStatusChecker.ConnectionCallback() {
            @Override
            public void callback(boolean isConnected) {
                handleConnection(isConnected);
            }
        });
    }

    @Override
    public void handleConnection(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(HomeActivity.this, NoWifiConnectionActivity.class));
            finish();
        }
    }
}