package com.example.helpme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;
import com.example.helpme.model.Duda;
import com.example.helpme.model.Materia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import controller.CursoController;

public class PublicarDudaActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText titulo;
    private EditText descripcion;
    private FirebaseFirestore myFirebase;

    private CursoController cursoController = new CursoController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_duda);
        spinner = (Spinner)findViewById(R.id.spinnerAsignaturas);
        titulo = (EditText)findViewById(R.id.editTextTituloDudaNueva);
        descripcion = (EditText)findViewById(R.id.editTextDuda);
        myFirebase = FirebaseFirestore.getInstance();
        loadAsignaturas();
        Button btnPublicar = (Button) findViewById(R.id.buttonpublicar);

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()){
                    crearDuda();
                    Snackbar.make(findViewById(R.id.layaoutPublicarDuda),R.string.dudaPublicada, Snackbar.LENGTH_LONG).show();
                }else {
                    Snackbar.make(findViewById(R.id.layaoutPublicarDuda),R.string.camposRellenados, Snackbar.LENGTH_LONG).show();
                }

            }
        });


    }

    private void crearDuda() {
        List<Asignatura> list = new ArrayList<>();
        String fecha = sacarFecha();
        Alumno a = new Alumno("","","","",list);
        //CAmbiar porqeue cambio constructor duda
        Asignatura asig = (Asignatura) spinner.getSelectedItem();
        Duda duda = new Duda(titulo.getText().toString(),descripcion.getText().toString(),a
                ,asig,asig.getMateria(),false,fecha);

        myFirebase.collection("DUDA")
                .add(duda)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        titulo.setText("");
        descripcion.setText("");
    }

    private String sacarFecha() {
        String fecha;
        Calendar c = new GregorianCalendar();
        fecha = c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR)+
            "/ "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        return fecha;
    }

    private boolean validarCampos() {
        if (titulo.getText().toString().equals("") || descripcion.getText().toString().equals("")
            || spinner.getSelectedItem().toString().equals("Sin definir")){
            return false;
        }
        return true;
    }

    public void loadAsignaturas(){
        List<Asignatura> asignaturas = new ArrayList<Asignatura>();
        final Curso[] cursoAsi = new Curso[1];
        final Materia[] materiaAsi = new Materia[1];
        Optional<Curso> curso2;
        //Cambiar xq cabio constructor asignatura
        //asignaturas.add(new Asignatura("a","b","Sin definir"));
        myFirebase.collection("ASIGNATURA")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference curso = (DocumentReference) document.get("curso");
                                DocumentReference materia = (DocumentReference) document.get("materia");
                                String nombre = document.getData().get("nombre").toString();
                                myFirebase.collection("CURSO")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.getId().equals(curso.getId())) {
                                                            String numero = document.get("numero").toString();
                                                            cursoAsi[0] = new Curso(document.getId(), numero);
                                                        }
                                                    }
                                                } else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                                myFirebase.collection("MATERIA")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        if (document.getId().equals(materia.getId())) {
                                                                            String denomiancion = document.get("denominacion").toString();
                                                                            String abreviatura = document.get("abreviatura").toString();
                                                                            materiaAsi[0] = new Materia(document.getId(), denomiancion,abreviatura);
                                                                        }
                                                                    }
                                                                    //Cambiat cambio constrcutor asignatura

                                                                    asignaturas.add(new Asignatura("1",nombre, (Curso)cursoAsi[0], (Materia)materiaAsi[0]));
                                                                    Log.d("Hola debug", document.getId() + " => " + document.getData());
                                                                    ArrayAdapter<Asignatura> arrayAdapter =  new ArrayAdapter<Asignatura>(PublicarDudaActivity.this
                                                                            , android.R.layout.simple_dropdown_item_1line,asignaturas);
                                                                    spinner.setAdapter(arrayAdapter);
                                                                } else {
                                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                                }
                                                            }
                                                        });
                                            }
                               });


                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}