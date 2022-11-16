package com.example.helpme;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Duda;
import com.example.helpme.navigation.ActivityNavigation;
import com.example.helpme.navigation.impl.ActivityNavigationImpl;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapter.DudaAdapter;
import auth.Authentication;
import dto.DudaDto;
import viewmodel.DudaViewModel;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HOME_ACTIVITY";

    private TextView txDayOfTheWeek;
    private TextView txDateFormatted;
    private Button btVerTodasDudas;
    private RecyclerView listadoDudasHomeRecycler;
    private Button btNuevaDuda;

    private DudaAdapter dudaAdapter;
    private DudaViewModel dudaViewModel = new DudaViewModel();

    private List<DudaDto> dudas = new ArrayList<>();

    private ActivityNavigationImpl navigation = new ActivityNavigationImpl();
    private BottomNavigationView navegacion;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initCalendarData();

        btVerTodasDudas = (Button) findViewById(R.id.btVerTodasDudas);
        btNuevaDuda = (Button) findViewById(R.id.bt_nueva_duda_home); // Es un layout no un boton
        listadoDudasHomeRecycler = (RecyclerView) findViewById(R.id.listado_dudas_home_recycler);

        listadoDudasHomeRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listadoDudasHomeRecycler.setLayoutManager(layoutManager);

        cargarDudas();

        dudaAdapter = new DudaAdapter(dudas);
        listadoDudasHomeRecycler.setAdapter(dudaAdapter);


        //Navegación
        navegacion = findViewById(R.id.bottomNavigationView);
        navegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_chat:

                        return true;
                    case R.id.nav_cuenta:
                        redirectPantallaCuenta();
                        return true;
                    case R.id.nav_dudas:
                        redirectPantallaDudas();
                        return true;
                }
                return false;
            }
        });

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

        // TODO: No borrar este evento
//        btLogoutDemo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // TODO: Mover a cuenta de usuario funcionalidad logout
////                Authentication.getInstance().signOut();
////                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
////                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
////                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                startActivity(intent);
////                finish();
//
//                Authentication.getInstance().sendEmailVerification();
//
//                // TODO: Plantilla correo
//                // https://support.google.com/firebase/answer/7000714
//
//            }
//        });
    }

    /**
     * Redirecciona al Activity ListarDudasActivity.
     */
    private void redirectPantallaListadoDudas() {
        Intent listadoDudasIntent = new Intent(HomeActivity.this, ListarDudasActivity.class);
        // Para transiciones
         startActivity(listadoDudasIntent);

        //startActivity(listadoDudasIntent);
    }

    /**
     * Redirecciona al Activity PublicarDudasActivity.
     */
    private void redirectPantallaPublicarNuevaDuda() {
        Intent publicarDudasIntent = new Intent(HomeActivity.this, PublicarDudaActivity.class);

        // Para transiciones
         startActivity(publicarDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(publicarDudasIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        cargarDudas();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarDudas() {
        dudas.clear();

        dudaViewModel.getAllDudas().observe(this, dudasResult -> {
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

            dudaAdapter = new DudaAdapter(dudas);
            listadoDudasHomeRecycler.setAdapter(dudaAdapter);

            dudaAdapter.notifyDataSetChanged();
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

    private void cargarDudasInit() {
        Duda d1 = new Duda("Duda 1", "asdfasfd", "asdfasdf", "000", "999", false, "20/10/2022 12:00:01");
//        dudas.add(d1);
    }

    private void redirectPantallaCuenta() {
        Intent listadoDudasIntent = new Intent(HomeActivity.this, ProfileActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaDudas() {
        Intent listadoDudasIntent = new Intent(HomeActivity.this, ListarDudasActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }
}