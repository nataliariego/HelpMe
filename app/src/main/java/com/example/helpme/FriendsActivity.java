package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Duda;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import adapter.AlumnoAdapter;
import adapter.DudaAdapter;
import dto.AlumnoDto;
import dto.DudaDto;
import viewmodel.AlumnoViewModel;
import viewmodel.DudaViewModel;

public class FriendsActivity extends AppCompatActivity {


    public static final String TAG = "FRIENDS_ACTIVITY";
    private static final String ALUMNO_SELECCIONADO ="ALUMNO_SELECCIONADO" ;

    private RecyclerView listadoAmigos;

    //Base de datos
    private AlumnoAdapter alumnoAdapter;
    private AlumnoViewModel alumnoViewModel = new AlumnoViewModel();

    private List<AlumnoDto> amigos = new ArrayList<>();

    private BottomNavigationView navegacion;


    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listadoAmigos = (RecyclerView) findViewById(R.id.reciclerViewAmigos);


        listadoAmigos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listadoAmigos.setLayoutManager(layoutManager);

        cargarAmigos();


        //Navegación
        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarAmigos() {
        amigos.clear();
        String email = userInSession.getEmail();
        alumnoViewModel.getAllAlumnosFriendsActivity().observe(this, alumnosResult -> {
            if (alumnosResult != null) {
                alumnosResult.forEach(d -> {
                    if (!d.getEmail().equalsIgnoreCase(email)) {
                        AlumnoDto newDuda = new AlumnoDto();
                        newDuda.nombre = d.getNombre();
                        newDuda.uo = d.getUo();
                        newDuda.urlFoto = d.getUrl_foto();
                        newDuda.asignaturasDominadas = d.getAsignaturasDominadas();
                        newDuda.email=d.getEmail();

                        amigos.add(newDuda);
                    }
                });
            }
            alumnoAdapter = new AlumnoAdapter(amigos);


            listadoAmigos.setAdapter(alumnoAdapter);
            alumnoAdapter.notifyDataSetChanged();
        });
    }


}