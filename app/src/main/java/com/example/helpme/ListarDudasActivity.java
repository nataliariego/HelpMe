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

import com.example.helpme.datos.AlumnoDataSource;
import com.example.helpme.datos.AsignaturaDataSource;
import com.example.helpme.datos.DudaDataSource;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Duda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListarDudasActivity extends AppCompatActivity {

    private static final String DUDA_SELECCIONADA = "duda_seleccionada";

    //Modelo de datos
    private List<Duda> listaDuda = new ArrayList<Duda>();;
    private Duda duda;
    private RecyclerView listaDudaView;
    private Object[] asignatura_data;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private AsignaturaDataSource asignaturaDataSource = new AsignaturaDataSource();
    private AlumnoDataSource alumnoDataSource = new AlumnoDataSource();
    private DudaDataSource dudaDataSource = new DudaDataSource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lilstardudas_main);
        //Rellenar lista de dudas
        listaDuda = cargarDudas();

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

    private ArrayList<Duda> cargarDudas() {
        ArrayList<Duda> listaDudaAux = new ArrayList<Duda>();
        listaDudaAux.add(new Duda( "una duda", "manolo", "fecha",
                 "asignatura",  true,  "url_foto_persona", "descripcion"));
        db.collection("DUDA")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Object[] campos_duda = document.getData().values().toArray();
                                Duda d = new Duda( campos_duda[3].toString(),
                                        alumnoDataSource.getAlumnoById(campos_duda[4].toString()).getNombre(),
                                        getFechaByTimestamp(campos_duda[2].toString()),
                                        asignaturaDataSource.getAsignaturaById(campos_duda[0].toString()).getNombre(),
                                        getBoolean(campos_duda[1].toString()),
                                        alumnoDataSource.getAlumnoById(campos_duda[4].toString()).getUrl_foto(), campos_duda[5].toString());
                                listaDudaAux.add(d);
                                System.out.println("TITULO" + " => " + campos_duda[3]);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return listaDudaAux;
    }

    private boolean getBoolean(String toString) {
        if (toString.equals("true")) return true;
        return false;
    }

    private String getFechaByTimestamp(String toString) {
        return "hola";
    }


}