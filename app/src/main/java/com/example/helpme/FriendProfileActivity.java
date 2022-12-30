package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Alumno;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controller.AlumnoController;
import de.hdodenhof.circleimageview.CircleImageView;
import dto.AsignaturaDto;
import viewmodel.AsignaturaViewModel;

public class FriendProfileActivity extends AppCompatActivity {


    private BottomNavigationView navegacion;

    private String alumno;
    private String email;
    private TextView textViewUO;
    private TextView textViewEmail;
    private CircleImageView img_persona;
    private EditText nombreCompleto;

    private ViewGroup.LayoutParams paramsBefore;

    private List<CheckBox> cB = new ArrayList<>();

    private List<String> asignaturaList = new ArrayList<String>();
    private AlumnoController alumnoController = new AlumnoController();

    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Intent intentAlumno= getIntent();
        alumno = intentAlumno.getStringExtra("alumno_seleccionado");

        textViewUO = findViewById(R.id.textViewUO);
        textViewEmail = findViewById(R.id.textViewEmail);
        img_persona = findViewById(R.id.img_persona_duda);
        nombreCompleto = findViewById(R.id.tv_user_name);


        String[] info = alumno.split("---");

        email = info[3];

        textViewUO.setText(info[1]);
        textViewEmail.setText(info[3]);
        nombreCompleto.setText(info[0]);
        if (info[2]!=null && info[2]!="")
            Picasso.get().load(info[2]).into(img_persona);

        cargarAsignaturas();

        paramsBefore=img_persona.getLayoutParams();

        //Cuando hago click fuera de la imagen
        findViewById(R.id.containerView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_persona.setLayoutParams(paramsBefore);
                findViewById(R.id.containerView).setBackgroundColor(Color.WHITE);
                findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                navegacion.setVisibility(View.INVISIBLE);
            }
        });

        //Cuando hago click dentro de la imagen
        img_persona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    img_persona.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    findViewById(R.id.containerView).setBackgroundColor(Color.GRAY);
                    findViewById(R.id.constraintLayout).setVisibility(View.INVISIBLE);
                }
        });


        //Navegacion:
        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());

    }



    private void cargarAsignaturas() {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        AsignaturaDto a = new AsignaturaDto();
                        a.nombre = d.getNombre();
                        a.curso=d.getCurso();
                        a.materia=d.getMateria();
                        a.id=d.getId();

                        asignaturaList.add(a.nombre);
                    });
                }
            }


            LinearLayout ll = findViewById(R.id.ll_dentroscroll);

            for (String a : asignaturaList) {
                CheckBox opcion = new CheckBox(this);
                opcion.setEnabled(false);
                opcion.setText(a);

                opcion.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                //Seleccionar si ya la tengo

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    alumnoController.findByUOWithPhoto(email, new AlumnoController.AlumnoCallback() {
                        @Override
                        public void callback(Alumno alumno) {
                            if (alumno != null) {
                                List<String> asignaturasAlumno = crearAsignaturas(alumno.getAsignaturasDominadas());

                                for (String as: asignaturasAlumno) {

                                    if (a.equals(as)) {

                                        opcion.setChecked(true);
                                        opcion.setTextColor(Color.BLACK);

                                    }
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


    private List<String> crearAsignaturas(Map<String, Object> asignaturasDominadas) {

        List<String> asignaturas = new ArrayList<>();
        Object[] asigs = asignaturasDominadas.values().toArray();
        for (Object nombre: asigs) {
            AsignaturaDto a = new AsignaturaDto();

            Map<Object, String> prueba = (Map<Object, String>) nombre;

            Object curso = prueba.get("curso");
            Object materia = prueba.get("materia");
            Object nombres = prueba.get("nombre");
            Object id = prueba.get("id");

            a.curso= curso.toString();
            a.materia= materia.toString();
            a.nombre= nombres.toString();
            if (id!=null)
                a.id= id.toString();

            asignaturas.add(a.nombre);
        }

        return asignaturas;

    }
}