package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import dto.AsignaturaDto;
import dto.CursoDto;
import dto.DudaDto;
import viewmodel.AsignaturaViewModel;
import viewmodel.CursoViewModel;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "profile_activity";
    private List<AsignaturaDto> asignaturaList = new ArrayList<AsignaturaDto>();
    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();
    private Spinner spinnerAsignaturas;
    private List<String> nombreAsignaturas = new ArrayList<>();

    private List<CursoDto> cursos = new ArrayList<CursoDto>();
    private CursoViewModel cursoViewModel = new CursoViewModel();
    private Spinner spinnerCursos;
    private List<String> numeroCursos = new ArrayList<>();
    private BottomNavigationView navegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cargarAsignaturas();
        cargarCursos();

        //navegacion
        navegacion = findViewById(R.id.bottomNavigationView);
        navegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_chat:

                        return true;
                    case R.id.nav_home:
                        redirectPantallaHome();
                        return true;
                    case R.id.nav_dudas:

                        return true;
                }
                return false;
            }
        });

    }

    private void cargarCursos() {
        cursoViewModel.getAllCursos().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i(TAG, d.getNumero());
                        CursoDto a = new CursoDto();
                        a.numero = d.getNumero();

                        cursos.add(a);
                    });
                }
            }
            for (CursoDto dto: cursos
            ) {
                numeroCursos.add(dto.numero);
            }


            spinnerCursos = findViewById(R.id.spinnerCurso);
            spinnerCursos.setAdapter(new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, numeroCursos));

        });
    }

    private void cargarAsignaturas() {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i(TAG, d.getNombre());
                        AsignaturaDto a = new AsignaturaDto();
                        a.nombre = d.getNombre();

                        asignaturaList.add(a);
                    });
                }
            }

            for (AsignaturaDto dto: asignaturaList
            ) {
                nombreAsignaturas.add(dto.nombre);
            }

            spinnerAsignaturas = findViewById(R.id.spinnerAsignaturasProfile);
            spinnerAsignaturas.setAdapter(new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_selectable_list_item, nombreAsignaturas));

        });
    }

    private void redirectPantallaHome() {
        Intent listadoDudasIntent = new Intent(ProfileActivity.this, HomeActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }
}