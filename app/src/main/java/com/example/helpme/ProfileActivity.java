package com.example.helpme;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;
import com.example.helpme.model.Materia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controller.AlumnoController;
import de.hdodenhof.circleimageview.CircleImageView;
import dto.AsignaturaDto;
import viewmodel.AsignaturaViewModel;

public class ProfileActivity extends AppCompatActivity {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "profile_activity";
    private List<AsignaturaDto> asignaturaList = new ArrayList<AsignaturaDto>();
    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();

    private List<Map<String, Object>> asignaturasDominadas = new ArrayList<>();
    private List<Map<String, Object>> allAsignaturas = new ArrayList<>();


    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();
    private AlumnoController alumnoController = new AlumnoController();

    private TextView textViewUO;
    private TextView textViewEmail;
    private CircleImageView img_persona;
    private EditText nombreCompleto;

    private List<CheckBox> cB = new ArrayList<>();

    private BottomNavigationView navegacion;

    private ConstraintLayout btnGuardar;

    private Button btnAmigos;

    private List<AsignaturaDto> asignaturaDuda = new ArrayList<>();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewUO = findViewById(R.id.textViewUO);
        textViewEmail = findViewById(R.id.textViewEmail);
        img_persona = findViewById(R.id.img_persona_duda);
        nombreCompleto = findViewById(R.id.tv_user_name);
        btnGuardar = findViewById(R.id.bt_guardar_perfil);
        btnAmigos = findViewById(R.id.buttonVerAmigos);

        //Tengo que buscar el alumno que tenga ese email para poner después los datos
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
            @Override
            public void callback(Alumno alumno) {
                Log.d(TAG, alumno.getEmail() + " " + alumno.getUrl_foto());
                if (alumno != null) {
                    //Esto tdo no está bien porque en la base de datos
                    //Se guardan raro los datos, faltan cosas...etc

//                        textViewUO.setText(uo);
//                        textViewEmail.setText(userInSession.getEmail());

                    textViewUO.setText(alumno.getNombre());
                    textViewEmail.setText(alumno.getNombre() + "@uniovi.es");
                    nombreCompleto.setText(alumno.getUo());
                    if (alumno.getUrl_foto() != null && alumno.getUrl_foto() != "")
                        Picasso.get().load(alumno.getUrl_foto()).into(img_persona);
                }
            }
        });
        //}

        // cargarAsignaturas();

        cargarAsignaturasDominadas();
        //navegacion
        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());

        //Boton guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarPerfil();
            }
        });

        //Boton ver amigos
        btnAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventanaAmigos();
            }
        });
    }

    private void ventanaAmigos() {
        Intent listadoDudasIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void actualizarPerfil() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                @Override
                public void callback(Alumno alumno) {
                    if (alumno != null) {
                        Map<String, Object> docData = new HashMap<>();
                        docData.put(Alumno.EMAIL, alumno.getEmail());
                        docData.put(Alumno.NOMBRE, nombreCompleto.getText().toString());
                        docData.put(Alumno.UO, alumno.getNombre());
                        docData.put(Alumno.URL_FOTO, alumno.getUrl_foto());

                        /*AlumnoDto a = new AlumnoDto();
                        a.email=alumno.getEmail();
                        a.uo=alumno.getNombre();
                        a.urlFoto=alumno.getUrl_foto();
                        a.nombre=nombreCompleto.getText().toString();
                        a.id=alumno.getId();
                        a.asignaturasDominadas=alumno.getAsignaturasDominadas();*/
                        //a.asignaturasDominadas=alumno.getAsignaturasDominadas();
                        asignaturasDominadas.clear();
                        for (CheckBox c : cB
                        ) {
                            if (c.isChecked()) {
                                Optional<Map<String, Object>> seleccionada = getAsignaturaDominadaByNombre(c.getText().toString());

                                if (seleccionada.isPresent()) {
                                    asignaturasDominadas.add(seleccionada.get());
                                }
                            }
                        }

                        Map<String, Object> mapAsi = new HashMap<>();

                        int i = 0;
                        for (Map<String, Object> as : asignaturasDominadas) {
//                        for (AsignaturaDto as : asignaturaDuda) {
                            mapAsi.put(String.valueOf(i++), as);
                        }

                        docData.put(Alumno.ASIGNATURAS_DOMINADAS, mapAsi);

                        System.out.println(docData.toString());

                        /*myFirebase.collection(Alumno.COLLECTION).document()
                                .set(docData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully actualizao!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error actualizando document", e);
                                    }
                                });
                        /*Log.i("*",a.toString());
                        asignaturaDuda.clear();
                        System.out.println(a.asignaturasDominadas);
                        alumnoController.update(a, a.id);*/

                        // TODO: Descomentar para actualizar perfil
                        db.collection(Alumno.COLLECTION).document(alumno.getId()).update(docData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Alumno actualizado");
                                        }
                                    }
                                });
                    }


                }
            });
        }

    }

    /**
     * Obtiene la asignatura dominada con el nombre indicado por parámetro.
     *
     * @param nombreAsignatura Nombre de la asignatura a buscar.
     * @return
     */
    private Optional<Map<String, Object>> getAsignaturaDominadaByNombre(String nombreAsignatura) {
        Map<String, Object> res = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return allAsignaturas.stream().filter(a -> a.get(Asignatura.NOMBRE).toString().equalsIgnoreCase(nombreAsignatura)).findFirst();
        }
        return null;
    }

    private void crearAsignaturaDuda(String nombreA) {
        asignaturaViewModel.getAllAsignaturasAsMap().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {

//                        if (d.nombre.equals(nombreA)) {
                        String nombre = d.get(Asignatura.NOMBRE).toString();

                        if (nombre.equalsIgnoreCase(nombreA)) {
                            Log.d(TAG, "SELECCIONADA: " + d.toString());
                            asignaturasDominadas.add(d);
                        }

//                            a.nombre = d.getNombre();
//                            a.id=d.getId();
//                            a.curso=d.getCurso();
//                            a.materia=d.getMateria();
//                            asignaturaDuda.add(a);
//                        }
                    });
                }
            }
        });
    }

    /*
    private void cargarCursos() {
        cursoViewModel.getAllCursos().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i(TAG, d.getNumero());
                        CursoDto a = new CursoDto();
                        a.numero = d.getNumero();

                        cursos.add(a);
                    });
                }
            }
            for (CursoDto dto: cursos
            ) {
                numeroCursos.add(dto.numero);
            }


            spinnerCursos.setAdapter(new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, numeroCursos));

        });
    }*/

    /**
     * Enhance.
     *
     * @since 29/12/2022
     */
    private void cargarAsignaturasDominadas() {
        asignaturaViewModel.getAllAsignaturasAsMap().observe(this, asigs -> {
            if (asigs != null) {

                /* Todas las asignaturas */
                for (Map<String, Object> asignatura : asigs) {
                    allAsignaturas.add(asignatura);
                }

                LinearLayout ll = findViewById(R.id.ll_dentroscroll);
                System.out.println("-->" + allAsignaturas.toString());


                /* Listado de checkboxes con las asignaturas disponibles */
                for (Map<String, Object> a : allAsignaturas) {
                    CheckBox opcion = new CheckBox(this);
                    opcion.setText(String.valueOf(a.get(Asignatura.NOMBRE)));

                    opcion.setLayoutParams(
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT));

                    String email = userInSession.getEmail();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                            @Override
                            public void callback(Alumno alumno) {
                                System.out.println("aa" + alumno);
                                if (alumno != null) {

                                    /* Marcar las asignaturas dominadas por el alumno */
                                    for (Map.Entry<String, Object> asigDom : alumno.getAsignaturasDominadas().entrySet()) {
                                        Map<String, Object> asignaturaDom = (Map<String, Object>) asigDom.getValue();
                                        if (String.valueOf(asignaturaDom.get(Asignatura.NOMBRE))
                                                .equalsIgnoreCase(a.get(Asignatura.NOMBRE).toString())) {
                                            opcion.setChecked(true);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    ll.addView(opcion);
                    cB.add(opcion);
                }
            }
        });
    }

    private void cargarAsignaturas() {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i(TAG, d.toString());
                        AsignaturaDto a = new AsignaturaDto();
                        a.nombre = d.getNombre();
                        a.curso = d.getCurso();
                        a.materia = d.getMateria();
                        a.id = d.getId();

                        asignaturaList.add(a);
                    });
                }
                System.out.println("-->" + asignaturaList);
            }


            LinearLayout ll = findViewById(R.id.ll_dentroscroll);
            System.out.println("-->" + asignaturaList);


            for (AsignaturaDto a : asignaturaList) {
                CheckBox opcion = new CheckBox(this);
                opcion.setText(a.nombre);
                opcion.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT));

                //Seleccionar si ya la tengo

                String email = userInSession.getEmail();

                System.out.println(".." + Build.VERSION.SDK_INT);
                System.out.println(".." + Build.VERSION_CODES.N);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                        @Override
                        public void callback(Alumno alumno) {
                            System.out.println("aa" + alumno);
                            if (alumno != null) {


                                List<AsignaturaDto> asignaturasAlumno = crearAsignaturas(alumno.getAsignaturasDominadas());


                                System.out.println("Las de el**" + asignaturasAlumno);
                                System.out.println("Todas**" + asignaturaList);

                                for (AsignaturaDto as : asignaturasAlumno) {
                                    if (a.equals(as)) opcion.setChecked(true);
                                }


                            }
                        }
                    });
                }


                ll.addView(opcion);
                cB.add(opcion);
            }
        });
    }

    private List<AsignaturaDto> crearAsignaturas(Map<String, Object> asignaturasDominadas) {

        List<AsignaturaDto> asignaturas = new ArrayList<>();
        Object[] asigs = asignaturasDominadas.values().toArray();

        Log.d(TAG, "ASIGS::: " + asignaturasDominadas.values().toString());

        for (Map.Entry asignatura : asignaturasDominadas.entrySet()) {
            Map<String, Object> asignaturaDom = (Map<String, Object>) asignatura.getValue();
            String nombre = (String) asignaturaDom.get(Asignatura.NOMBRE);
            String id = (String) asignaturaDom.get(Asignatura.ID);

            Map<String, Object> curso = (Map<String, Object>) asignaturaDom.get(Asignatura.CURSO);
            Map<String, Object> materia = (Map<String, Object>) asignaturaDom.get(Asignatura.MATERIA);

            AsignaturaDto newAsig = new AsignaturaDto();
            newAsig.id = id;
            newAsig.nombre = nombre;
            newAsig.curso = (String) curso.get(Curso.NUMERO);
            newAsig.materia = (String) materia.get(Materia.ABREVIATURA);

            asignaturas.add(newAsig);
        }

//        for (Object nombre: asigs) {
//            AsignaturaDto a = new AsignaturaDto();
//            String linea = nombre.toString();
//            System.out.println("pa" + nombre);
//           // a.curso=linea.split("curso=")[1].split(Pattern.quote("}")+",")[0]+"}";
//            //a.materia=linea.split("materia=")[1].split(Pattern.quote("}")+",")[0]+"}";
//            //a.nombre=linea.split("nombre=")[1].split(",")[0].split(Pattern.quote("}"))[0];
//            //esta nal el id
//
//            Map<Object, String> prueba = (Map<Object, String>) nombre;
//
//            prueba.get("id");
//            //System.out.println("prueba " + prueba.get("curso"));
//            a.curso=prueba.get("curso");
//            a.materia=prueba.get("materia");
//            a.nombre=prueba.get("nombre");
//            a.id=prueba.get("id");
//            asignaturas.add(a);
//        }

        return asignaturas;

    }
}