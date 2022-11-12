package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

import dto.AsignaturaDto;
import dto.DudaDto;
import viewmodel.AsignaturaViewModel;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "profile_activity";
    private List<AsignaturaDto> asignaturaList = new ArrayList<AsignaturaDto>();
    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();
    private Spinner spinnerAsignaturas;
    private List<String> nombreAsignaturas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cargarAsignaturas();



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

            Log.i("pepito", String.valueOf(nombreAsignaturas.size()));

            for (String s: nombreAsignaturas
            ) {
                Log.i("paaaaaaaaa", s);
            }

            spinnerAsignaturas = findViewById(R.id.spinnerAsignaturasProfile);
            spinnerAsignaturas.setAdapter(new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, nombreAsignaturas));

        });
    }
}