package com.example.helpme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.helpme.model.Duda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarDudasActivity extends AppCompatActivity {

    private static final String DUDA_SELECCIONADA = "duda_seleccionada";

    //Modelo de datos
    private List<Duda> listaDuda;
    private Duda duda;
    private RecyclerView listaDudaView;

    private FirebaseFirestore myFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lilstardudas_main);
        myFirebase = FirebaseFirestore.getInstance();
        //Rellenar lista de dudas
        cargarDudas();

        // Recuperamos referencia y configuramos recyclerView con la lista de dudas
        listaDudaView = (RecyclerView)findViewById(R.id.reciclerView);
        listaDudaView.setHasFixedSize(true);

        /* Un RecyclerView necesita un Layout Manager para manejar el posicionamiento de los
        elementos en cada línea. Se podría definir un LayoutManager propio extendendiendo la clase
        RecyclerView.LayoutManager. Sin embargo, en la mayoría de los casos, simplemente se usa
        una de las subclases LayoutManager predefinidas: LinearLayoutManager, GridLayoutManager,
        StaggeredGridLayoutManager*/
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaDudaView.setLayoutManager(layoutManager);

        //Pasamos la lista de dudas al RecyclerView con el ListaDudaAdapter
        // Instanciamos el adapter con los datos de la petición y lo asignamos a RecyclerView
        // Generar el adaptador, le pasamos la lista de dudas
        // y el manejador para el evento click sobre un elemento
        ListaDudasAdapter lpAdapter= new ListaDudasAdapter(listaDuda,
                new ListaDudasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Duda duda) {
                        clikonIntem(duda);
                    }
                });
        listaDudaView.setAdapter(lpAdapter);
    }

    //click del item del adapter
    private void clikonIntem(Duda duda) {
        Log.i("Click adapter","Item Clicked "+duda.getTitulo());


        //Le paso la duda al MainActivity para que la muestre al picnchar en la duda
        Intent intent=new Intent (ListarDudasActivity.this, ActivityShowDuda.class);
        intent.putExtra(DUDA_SELECCIONADA, duda);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void cargarDudas() {
        listaDuda = new ArrayList<Duda>();
        listaDuda.add(new Duda( "una duda", "manolo", "fecha",
                 "asignatura",  true,  "url_foto_persona"));
        Log.w(TAG, "HOLAAA");
        myFirebase.collection("DUDA")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}