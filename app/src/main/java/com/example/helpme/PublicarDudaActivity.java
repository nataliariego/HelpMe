package com.example.helpme;

import static android.content.ContentValues.TAG;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;
import com.example.helpme.model.Duda;
import com.example.helpme.model.Materia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controller.CursoController;

public class PublicarDudaActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText titulo;
    private EditText descripcion;
    private FirebaseFirestore myFirebase;
    private Button btnPublicar;

    private CursoController cursoController = new CursoController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_duda);
        spinner = (Spinner) findViewById(R.id.spinnerAsignaturas);
        titulo = (EditText) findViewById(R.id.editTextTituloDudaNueva);
        descripcion = (EditText) findViewById(R.id.editTextDuda);
        myFirebase = FirebaseFirestore.getInstance();
        BottomNavigationView navegacion = findViewById(R.id.bottomNavigationView);
        //TODO: loadAsignaturas();
        List<String> asignaturas1 = new ArrayList<>();
        asignaturas1.add("Estructuras de datos");
        asignaturas1.add("Bases de datos");
        asignaturas1.add("Comunicación Persona-Máquina");

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(PublicarDudaActivity.this
                , android.R.layout.simple_dropdown_item_1line, asignaturas1);
        spinner.setAdapter(arrayAdapter1);
        btnPublicar = (Button) findViewById(R.id.buttonpublicar);
        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    crearDuda();
                    Snackbar.make(findViewById(R.id.layaoutPublicarDuda), R.string.dudaPublicada, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(R.id.layaoutPublicarDuda), R.string.camposRellenados, Snackbar.LENGTH_LONG).show();
                }

            }
        });

        //Navegacion:
        navegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        redirectPantallaHome();
                        return true;
                    case R.id.nav_chat:

                        return true;
                    case R.id.nav_cuenta:

                        return true;
                    case R.id.nav_dudas:

                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
//                if (validarCampos()){
                Duda newDuda = new Duda();
                newDuda.setTitulo(titulo.getText().toString());
                newDuda.setDescripcion(descripcion.getText().toString());
                newDuda.setFecha(LocalDateTime.now().toString());
                newDuda.setResuelta(false);

                btnPublicar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validarCampos()) {
                            crearDuda();
                            Snackbar.make(findViewById(R.id.layaoutPublicarDuda), R.string.dudaPublicada, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(findViewById(R.id.layaoutPublicarDuda), R.string.camposRellenados, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearDuda() {
        List<Asignatura> list = new ArrayList<>();
        String fecha = sacarFecha();
        Alumno a = new Alumno("", "", "", "", list);
        //CAmbiar porqeue cambio constructor duda
        // TODO: Asignatura asig = (Asignatura) spinner.getSelectedItem();
        Map<String, Object> alumno = new HashMap<>();
        alumno.put("nombre", "Pepe");
        alumno.put("id", "");
        alumno.put("uo", "Pepe");
        alumno.put("url_foto", "aldsfasldfjasdf");
        alumno.put("asignaturasDominadas", new ArrayList<>());

        Map<String, Object> asignaturaMap = new HashMap<>();
        asignaturaMap.put("nombre", spinner.getSelectedItem().toString());
        asignaturaMap.put("curso", new HashMap<>());
        asignaturaMap.put("id", "2");
        asignaturaMap.put("materia", new HashMap<>());

        Map<String, Object> materia = new HashMap<>();
        materia.put("abreviatura", "");
        materia.put("denominacion", "");
        materia.put("id", "");

        Map<String, Object> docData = new HashMap<>();
        docData.put("titulo", titulo.getText().toString());
        docData.put("descripcion", descripcion.getText().toString());
        docData.put("alumno", alumno);
        docData.put("asignatura", asignaturaMap);
        docData.put("materia", materia);
        docData.put("resuelta", false);
        docData.put("fecha", fecha);


// todo:       Duda duda = new Duda(titulo.getText().toString(), descripcion.getText().toString(), alumno.toString()
//                , "ASIGNATURA/Pu86h2KJes5iydRSNEGl", asig.getMateria(), false, fecha);

//        Duda duda = new Duda(titulo.getText().toString(), descripcion.getText().toString(), alumno.toString()
//                , asignaturaMap.toString(), "", false, fecha);

//        myFirebase.collection("DUDA")
//                .add(duda)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

        myFirebase.collection(Duda.COLLECTION).document("one")
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        titulo.setText("");
        descripcion.setText("");
    }

    private String sacarFecha() {
        String fecha;
        Calendar c = new GregorianCalendar();
        fecha = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
                + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":00";
        return fecha;
    }

    private boolean validarCampos() {
        if (titulo.getText().toString().equals("") || descripcion.getText().toString().equals("")
                || spinner.getSelectedItem().toString().equals("Sin definir")) {
            return false;
        }
        return true;
    }

    public void loadAsignaturas() {
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
                                String id = document.getId();
                                String curso = document.getData().get("curso").toString();
                                String materia = document.getData().get("materia").toString();
                                String nombre = document.getData().get("nombre").toString();
                                System.out.println("holaaaaaa"+curso);
                                asignaturas.add(new Asignatura(id,nombre, curso, materia));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                        ArrayAdapter<Asignatura> arrayAdapter = new ArrayAdapter<Asignatura>(PublicarDudaActivity.this
                                , android.R.layout.simple_dropdown_item_1line, asignaturas);
                        spinner.setAdapter(arrayAdapter);
                    }
                });




    }

    private void redirectPantallaHome() {
        Intent listadoDudasIntent = new Intent(PublicarDudaActivity.this, HomeActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }
}