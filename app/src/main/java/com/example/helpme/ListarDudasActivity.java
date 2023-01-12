package com.example.helpme;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Duda;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import adapter.DudaAdapter;
import dto.DudaDto;
import viewmodel.DudaViewModel;

public class ListarDudasActivity extends AppCompatActivity {

    public static final String TAG = "LISTAR_DUDAS_ACTIVITY";
    public static final String DUDA_SELECCIONADA = "duda_seleccionada";

    private RecyclerView listaDudaView;
    private DudaAdapter dudaAdapter;
    private DudaViewModel dudaViewModel = new DudaViewModel();
    private List<DudaDto> dudas = new ArrayList<>();
    private List<String> idDudas = new ArrayList<>();
    private BottomNavigationView navegacion;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_dudas);

        listaDudaView = (RecyclerView) findViewById(R.id.recicler_listado_dudas);
        listaDudaView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaDudaView.setLayoutManager(layoutManager);


        /* Cargar todas las dudas */
        cargarDudas();

        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());
    }

    public void clikonIntem(DudaDto duda) {
        Duda dudaCreada = crearDuda(duda);
        Intent intent = new Intent(ListarDudasActivity.this, ResolveActivity.class);
        intent.putExtra(DUDA_SELECCIONADA, dudaCreada);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private Duda crearDuda(DudaDto duda) {
        Duda d = new Duda(duda.titulo, duda.descripcion, duda.alumno, duda.asignatura, duda.materia, duda.isResuelta, duda.fecha, duda.id, duda.url_adnjuto);
        return d;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    @Override
    protected void onResume() {
        super.onResume();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            redirectToLogin();

        }else{
            cargarDudas();
            dudaAdapter = new DudaAdapter(dudas,
                    new DudaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DudaDto duda) {
                            clikonIntem(duda);
                        }
                    });
            ;
            listaDudaView.setAdapter(dudaAdapter);

            dudaAdapter.notifyDataSetChanged();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarDudas() {
        dudas = new ArrayList<>();

        dudaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                dudasResult.forEach(d -> {
                    DudaDto newDuda = new DudaDto();
                    newDuda.titulo = d.getTitulo();
                    newDuda.descripcion = d.getDescripcion();
                    newDuda.alumno = d.getAlumnoId();
                    newDuda.asignatura = d.getAsignaturaId();
                    newDuda.fecha = d.getFecha();
                    newDuda.id = d.getId();
                    newDuda.materia = d.getMateriaId();
                    newDuda.isResuelta = d.isResuelta();
                    newDuda.url_adnjuto = d.getUrl_adjunto();

                    dudas.add(newDuda);
                    dudas = dudas.stream().distinct().collect(Collectors.toList());
                });
            }
            dudaAdapter = new DudaAdapter(dudas,
                    new DudaAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DudaDto duda) {
                            clikonIntem(duda);
                        }
                    });
            ;
            listaDudaView.setAdapter(dudaAdapter);
            dudaAdapter.notifyDataSetChanged();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ListarDudasActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}