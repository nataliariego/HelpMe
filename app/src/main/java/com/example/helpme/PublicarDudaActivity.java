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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import assembler.CursoAssembler;
import assembler.MateriaAssembler;
import controller.AlumnoController;
import controller.CursoController;
import dto.AlumnoDto;
import dto.AsignaturaDto;
import dto.CursoDto;
import dto.MateriaDto;
import viewmodel.AsignaturaViewModel;
import viewmodel.CursoViewModel;
import viewmodel.MateriaViewModel;

public class PublicarDudaActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText titulo;
    private EditText descripcion;
    private FirebaseFirestore myFirebase;
    private Button btnPublicar;
    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();
    private List<String> nombreAsignaturas = new ArrayList<>();
    private List<AsignaturaDto> asignaturaList = new ArrayList<AsignaturaDto>();
    private CursoController cursoController = new CursoController();
    private List<AsignaturaDto> asignaturaDuda = new ArrayList<>();
    private CursoViewModel cursoViewModel = new CursoViewModel();
    private List<CursoDto> cursos = new ArrayList<CursoDto>();
    private MateriaViewModel materiaViewModel = new MateriaViewModel();
    private List<MateriaDto> materias =  new ArrayList<>();
    private AlumnoController alumnoController = new AlumnoController();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_duda);
        spinner = (Spinner) findViewById(R.id.spinnerAsignaturas);
        titulo = (EditText) findViewById(R.id.editTextTituloDudaNueva);
        descripcion = (EditText) findViewById(R.id.editTextDuda);
        myFirebase = FirebaseFirestore.getInstance();
        BottomNavigationView navegacion = findViewById(R.id.bottomNavigationView);
        cargarAsignaturas();
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
                        redirectPantallaCuenta();
                        return true;
                    case R.id.nav_dudas:
                        redirectPantallaDudas();
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
        //CAmbiar porqeue cambio constructor duda
        // TODO: Asignatura asig = (Asignatura) spinner.getSelectedItem();



        //Pongo los datos del usuario que está autenticado
        String email = userInSession.getEmail();

        // Log.i("patatita: " , uo);
        //Tengo que buscar el alumno que tenga ese email para poner después los datos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                @Override
                public void callback(Alumno alumno) {
                    if (alumno != null) {
                        Map<String, Object> alumnoMap = new HashMap<>();
                        //Esto tdo no está bien porque en la base de datos
                        //Se guardan raro los datos, faltan cosas...etc
                        alumnoMap.put("nombre", alumno.getUo());
                        alumnoMap.put("id", alumno.getId());
                        alumnoMap.put("uo", alumno.getNombre());
                        alumnoMap.put("url_foto", alumno.getUrl_foto());
                        alumnoMap.put("asignaturasDominadas", alumno.getAsignaturasDominadas());
                        Map<String, Object> asignaturaMap = new HashMap<>();
                        String nAsignatura = spinner.getSelectedItem().toString();
                        crearAsignaturaDuda(nAsignatura);

                        Map<String, Object> cursoMap = CursoAssembler.toHashMap(asignaturaDuda.get(0).curso);


                        Map<String, Object> materiaMap = MateriaAssembler.toHashMap(asignaturaDuda.get(0).materia);
                        asignaturaMap.put("nombre", nAsignatura);
                        asignaturaMap.put("curso", cursoMap);
                        asignaturaMap.put("id", asignaturaDuda.get(0).id);
                        asignaturaMap.put("materia", materiaMap);
                        asignaturaDuda.clear();


                        Map<String, Object> docData = new HashMap<>();
                        docData.put("titulo", titulo.getText().toString());
                        docData.put("descripcion", descripcion.getText().toString());
                        docData.put("alumno", alumnoMap);
                        docData.put("asignatura", asignaturaMap);
                        docData.put("materia", materiaMap);
                        docData.put("resuelta", false);
                        docData.put("fecha", fecha);


                        System.out.println("Holaaaaaaaaaa");
                        myFirebase.collection(Duda.COLLECTION).document()
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
                }
            });
        }


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

            spinner = findViewById(R.id.spinnerAsignaturas);
            spinner.setAdapter(new ArrayAdapter<>(PublicarDudaActivity.this, android.R.layout.simple_selectable_list_item, nombreAsignaturas));

        });
    }

    private void crearMateriaDuda(String abre) {
        materiaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        System.out.println("LLegdshfgshdgfskjhf");
                        Log.i(TAG, d.getAbreviatura());
                        if (abre.equals(d.getAbreviatura())){
                            MateriaDto a = new MateriaDto();
                            a.id = d.getId();
                            a.abreviatura=d.getAbreviatura();
                            a.denominacion=d.getDenominacion();

                            materias.add(a);
                        }

                    });
                }
            }
        });
    }

    private void crearAsignaturaDuda(String nombreA) {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {

                        AsignaturaDto a = new AsignaturaDto();
                        if (d.getNombre().equals(nombreA)){
                            a.nombre = d.getNombre();
                            a.id=d.getId();
                            a.curso=d.getCurso();
                            a.materia=d.getMateria();
                            asignaturaDuda.add(a);
                        }
                    });
                }
            }
        });
    }

    private void crearCursoDuda(String num) {
        cursoViewModel.getAllCursos().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i(TAG, d.getNumero());
                        if (num.equals(d.getNumero())) {
                            CursoDto a = new CursoDto();
                            a.numero = d.getNumero();
                            cursos.add(a);
                        }
                    });
                }
            }

        });
    }

    private void redirectPantallaHome() {
        Intent listadoDudasIntent = new Intent(PublicarDudaActivity.this, HomeActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaDudas() {
        Intent listadoDudasIntent = new Intent(PublicarDudaActivity.this, ListarDudasActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaCuenta() {
        Intent listadoDudasIntent = new Intent(PublicarDudaActivity.this, ProfileActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }
}