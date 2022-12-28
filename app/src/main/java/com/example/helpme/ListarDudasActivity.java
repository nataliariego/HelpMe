package com.example.helpme;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Duda;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import adapter.DudaAdapter;
import dto.DudaDto;
import viewmodel.DudaViewModel;

public class ListarDudasActivity extends AppCompatActivity {

    public static final String TAG = "LISTAR_DUDAS_ACTIVITY";

    public static final String DUDA_SELECCIONADA = "duda_seleccionada";

    //Modelo de datos
    private List<Duda> listaDuda = new ArrayList<Duda>();


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


        // Recuperamos referencia y configuramos recyclerView con la lista de dudas
        listaDudaView = (RecyclerView) findViewById(R.id.recicler_listado_dudas);
        listaDudaView.setHasFixedSize(true);

        /* Un RecyclerView necesita un Layout Manager para manejar el posicionamiento de los
        elementos en cada línea. Se podría definir un LayoutManager propio extendendiendo la clase
        RecyclerView.LayoutManager. Sin embargo, en la mayoría de los casos, simplemente se usa
        una de las subclases LayoutManager predefinidas: LinearLayoutManager, GridLayoutManager,
        StaggeredGridLayoutManager*/
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaDudaView.setLayoutManager(layoutManager);


        //Rellenar lista de dudas y en el adapter
        cargarDudas();
        //Pasamos la lista de dudas al RecyclerView con el ListaDudaAdapter
        // Instanciamos el adapter con los datos de la petición y lo asignamos a RecyclerView
        // Generar el adaptador, le pasamos la lista de dudas
        // y el manejador para el evento click sobre un elemento

        //Navegacion:
        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());


    }

    public void clikonIntem(DudaDto duda) {
        Duda dudaCreada = crearDuda(duda);
        //Paso el modo de apertura
        Intent intent = new Intent(ListarDudasActivity.this, ResolveActivity.class);
        intent.putExtra(DUDA_SELECCIONADA, dudaCreada);
        //Transacion de barrido
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private Duda crearDuda(DudaDto duda) {
        Duda d = new Duda(duda.titulo, duda.descripcion, duda.alumno, duda.asignatura, duda.materia, duda.isResuelta, duda.fecha, duda.id,duda.url_adnjuto);
        return d;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    @Override
    protected void onResume() {
        super.onResume();


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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarDudas() {

        dudas = new ArrayList<>();

        dudaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                dudasResult.forEach(d -> {
                    Log.i(TAG, d.getTitulo() + " " + d.getAlumnoId());
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


    private void redirectPantallaHome() {
        Intent listadoDudasIntent = new Intent(ListarDudasActivity.this, HomeActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaDudas() {
        Intent listadoDudasIntent = new Intent(ListarDudasActivity.this, ListarDudasActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaCuenta() {
        Intent listadoDudasIntent = new Intent(ListarDudasActivity.this, ProfileActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }


}