package com.example.helpme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.helpme.model.Asignatura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PublicarDudaActivity extends AppCompatActivity {

    Spinner spinner;

    private FirebaseFirestore myFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_duda);
        spinner = (Spinner)findViewById(R.id.spinnerAsignaturas);

        myFirebase = FirebaseFirestore.getInstance();
        loadAsignaturas();



    }

    public void loadAsignaturas(){
        List<Asignatura> asignaturas = new ArrayList<Asignatura>();
        asignaturas.add(new Asignatura("a","b","Sin definir"));
        myFirebase.collection("ASIGNATURA")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id_materia = document.getData().get("ID_MATERIA").toString();
                                String id_curso = document.getData().get("ID_CURSO").toString();
                                String nombre = document.getData().get("NOMBRE").toString();
                                asignaturas.add(new Asignatura(id_curso,id_materia,nombre));
                                Log.d("Hola debug", document.getId() + " => " + document.getData());
                            }
                            ArrayAdapter<Asignatura> arrayAdapter =  new ArrayAdapter<Asignatura>(PublicarDudaActivity.this
                            , android.R.layout.simple_dropdown_item_1line,asignaturas);
                            spinner.setAdapter(arrayAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}