package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import controller.AlumnoController;
import de.hdodenhof.circleimageview.CircleImageView;
import dto.AsignaturaDto;
import dto.CursoDto;
import dto.DudaDto;
import viewmodel.AsignaturaViewModel;
import viewmodel.CursoViewModel;

import com.example.helpme.model.Alumno;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

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

    private BottomNavigationView navegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewUO = findViewById(R.id.textViewUO);
        textViewEmail = findViewById(R.id.textViewEmail);
        img_persona = findViewById(R.id.img_persona_duda);
        nombreCompleto = findViewById(R.id.tv_user_name);

        //Pongo los datos del usuario que está autenticado
        String uo = userInSession.getEmail().split("@")[0].toUpperCase();

       // Log.i("patatita: " , uo);
        //Tengo que buscar el alumno que tenga ese email para poner después los datos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alumnoController.findByUOWithPhoto(uo, new AlumnoController.AlumnoCallback() {
                @Override
                public void callback(Alumno alumno) {
                    if (alumno != null) {
                        //Esto tdo no está bien porque en la base de datos
                        //Se guardan raro los datos, faltan cosas...etc
                        Log.i("patita", alumno.toString());
                        textViewUO.setText(alumno.getNombre());
                        textViewEmail.setText(alumno.getNombre()+"@uniovi.es");
                        nombreCompleto.setText(alumno.getUo());
                        Picasso.get().load(alumno.getUrl_foto()).into(img_persona);
                    }
                }
            });
        }


        cargarAsignaturas();
        cargarCursos();


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

    }

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


            spinnerCursos = findViewById(R.id.spinnerCurso);
            spinnerCursos.setAdapter(new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, numeroCursos));

        });
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

            LinearLayout ll = findViewById(R.id.ll_dentroscroll);

            //ScrollView s = findViewById(R.id.scrollView2);
            //spinnerAsignaturas = findViewById(R.id.spinnerAsignaturasProfile);
            //spinnerAsignaturas.setAdapter(new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_selectable_list_item, nombreAsignaturas));
            for (String a : nombreAsignaturas) {
                CheckBox opcion = new CheckBox(this);
                opcion.setText(a);
                opcion.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.addView(opcion);
            }
        });
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