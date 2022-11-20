package com.example.helpme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import assembler.MateriaAssembler;
import controller.AlumnoController;
import de.hdodenhof.circleimageview.CircleImageView;
import dto.AlumnoDto;
import dto.AsignaturaDto;
import dto.CursoDto;
import dto.DudaDto;
import viewmodel.AsignaturaViewModel;
import viewmodel.CursoViewModel;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Duda;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {



    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "profile_activity";
    private List<AsignaturaDto> asignaturaList = new ArrayList<AsignaturaDto>();
    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();
    private Spinner spinnerAsignaturas;
    private List<String> nombreAsignaturas = new ArrayList<>();

    private List<CursoDto> cursos = new ArrayList<CursoDto>();
    private CursoViewModel cursoViewModel = new CursoViewModel();
    private Spinner spinnerCursos;
    private List<String> numeroCursos = new ArrayList<>();

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

        //Pongo los datos del usuario que está autenticado
        String uo = userInSession.getEmail().split("@")[0].toUpperCase();

        Log.i(TAG, "UO: " + uo + " ; email: " + userInSession.getEmail());

       // Log.i("patatita: " , uo);
        //Tengo que buscar el alumno que tenga ese email para poner después los datos
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                @Override
                public void callback(Alumno alumno) {
                    if (alumno != null) {
                        //Esto tdo no está bien porque en la base de datos
                        //Se guardan raro los datos, faltan cosas...etc

//                        textViewUO.setText(uo);
//                        textViewEmail.setText(userInSession.getEmail());

                        textViewUO.setText(alumno.getNombre());
                        textViewEmail.setText(alumno.getNombre()+"@uniovi.es");
                        nombreCompleto.setText(alumno.getUo());
                        if (alumno.getUrl_foto()!=null &&  alumno.getUrl_foto()!="")
                            Picasso.get().load(alumno.getUrl_foto()).into(img_persona);
                    }
                }
            });
        //}


        cargarAsignaturas();
        //cargarCursos();


        //navegacion
        navegacion = findViewById(R.id.bottomNavigationView);
        navegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_chat:

                        return true;
                    case R.id.nav_home:
                        redirectPantallaHome();
                        return true;
                    case R.id.nav_dudas:
                        redirectPantallaDudas();
                        return true;
                }
                return false;
            }
        });

        //Boton guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarPerfil();
            }
        });

        //Boton guardar
        btnAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventanaAmigos();
            }
        });
    }

    private void ventanaAmigos(){
        Intent listadoDudasIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void actualizarPerfil() {

        String uo = userInSession.getEmail().split("@")[0].toUpperCase();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                @Override
                public void callback(Alumno alumno) {
                    if (alumno != null) {
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("email", alumno.getEmail());
                        docData.put("nombre",nombreCompleto.getText().toString());
                        docData.put("uo", alumno.getNombre());
                        docData.put("url_foto", alumno.getUrl_foto());

                        /*AlumnoDto a = new AlumnoDto();
                        a.email=alumno.getEmail();
                        a.uo=alumno.getNombre();
                        a.urlFoto=alumno.getUrl_foto();
                        a.nombre=nombreCompleto.getText().toString();
                        a.id=alumno.getId();
                        a.asignaturasDominadas=alumno.getAsignaturasDominadas();*/
                        //a.asignaturasDominadas=alumno.getAsignaturasDominadas();
                        for (CheckBox c: cB
                             ) {
                            if (c.isChecked())
                            {
                                crearAsignaturaDuda(c.getText().toString());
                            }
                        }
                        Map<String, Object> mapAsi = new HashMap<>();

                        int i=1;
                        for (AsignaturaDto as: asignaturaDuda
                             ) {
                            //System.out.println("-->"+as.toString());
                            mapAsi.put(String.valueOf(i), as);
                            i++;
                        }

                        docData.put("asignaturasDominadas", mapAsi);

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

                        db.collection(Alumno.COLLECTION).document(alumno.getId()).update(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "Alumno actualizado");
                                }
                            }
                        });
                    }



                }
            });
        }

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

    private void cargarAsignaturas() {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i(TAG, d.getNombre());
                        AsignaturaDto a = new AsignaturaDto();
                        a.nombre = d.getNombre();
                        a.curso=d.getCurso();
                        a.materia=d.getMateria();
                        a.id=d.getId();

                        asignaturaList.add(a);
                    });
                }
            }



            LinearLayout ll = findViewById(R.id.ll_dentroscroll);



            for (AsignaturaDto a : asignaturaList) {
                CheckBox opcion = new CheckBox(this);
                opcion.setText(a.nombre);
                opcion.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                //Seleccionar si ya la tengo

                String uo = userInSession.getEmail().split("@")[0].toUpperCase();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    alumnoController.findByUOWithPhoto(uo, new AlumnoController.AlumnoCallback() {
                                @Override
                                public void callback(Alumno alumno) {
                                    if (alumno != null) {


                                        List<AsignaturaDto> asignaturasAlumno = crearAsignaturas(alumno.getAsignaturasDominadas());


                                        System.out.println("Las de el**"+ asignaturasAlumno);
                                        System.out.println("Todas**"+asignaturaList);

                                        for (AsignaturaDto as: asignaturasAlumno) {
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
        for (Object nombre: asigs) {
            AsignaturaDto a = new AsignaturaDto();
            String linea = nombre.toString();
            System.out.println("pa" + nombre);
           // a.curso=linea.split("curso=")[1].split(Pattern.quote("}")+",")[0]+"}";
            //a.materia=linea.split("materia=")[1].split(Pattern.quote("}")+",")[0]+"}";
            //a.nombre=linea.split("nombre=")[1].split(",")[0].split(Pattern.quote("}"))[0];
            //esta nal el id

            Map<Object, String> prueba = (Map<Object, String>) nombre;

            prueba.get("id");
            System.out.println("prueba " + prueba.get("curso"));
            a.curso=prueba.get("curso");
            a.materia=prueba.get("materia");
            a.nombre=prueba.get("nombre");
            a.id=prueba.get("id");
            asignaturas.add(a);
        }


        return asignaturas;

    }


    private void redirectPantallaHome() {
        Intent listadoDudasIntent = new Intent(ProfileActivity.this, HomeActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaDudas() {
        Intent listadoDudasIntent = new Intent(ProfileActivity.this, ListarDudasActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }
}