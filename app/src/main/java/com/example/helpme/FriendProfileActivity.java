package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.helpme.model.Alumno;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import controller.AlumnoController;
import de.hdodenhof.circleimageview.CircleImageView;
import dto.AlumnoDto;
import dto.AsignaturaDto;
import viewmodel.AsignaturaViewModel;

public class FriendProfileActivity extends AppCompatActivity {



    private String alumno;
    private String email;
    private TextView textViewUO;
    private TextView textViewEmail;
    private CircleImageView img_persona;
    private EditText nombreCompleto;

    private ViewGroup.LayoutParams paramsBefore;

    boolean isImageFitToScreen;

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


        Log.i("patata", alumno);

        String[] info = alumno.split("---");

        email = info[3];

        textViewUO.setText(info[1]);
        textViewEmail.setText(info[3]);
        nombreCompleto.setText(info[0]);
        if (info[2]!=null && info[2]!="")
            Picasso.get().load(info[2]).into(img_persona);

        cargarAsignaturas();

        paramsBefore=img_persona.getLayoutParams();


        findViewById(R.id.containerView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i("-->", "a ver");
                img_persona.setLayoutParams(paramsBefore);
                //img_persona.layout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                img_persona.setMinimumHeight(124);
                img_persona.setMaxHeight(124);
                findViewById(R.id.containerView).setBackgroundColor(Color.WHITE);
                findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);



            }
        });
        img_persona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    img_persona.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    findViewById(R.id.containerView).setBackgroundColor(Color.GRAY);
                    findViewById(R.id.constraintLayout).setVisibility(View.INVISIBLE);
                    //img_persona.setScaleType(CircleImageView.ScaleType.FIT_XY);
                }

        });

    }


    private void cargarAsignaturas() {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {
                        Log.i("FriendProfileActivity", d.getNombre());
                        AsignaturaDto a = new AsignaturaDto();
                        a.nombre = d.getNombre();
                        a.curso=d.getCurso();
                        a.materia=d.getMateria();
                        a.id=d.getId();

                        asignaturaList.add(a.nombre);
                    });
                }
                System.out.println("-->"+asignaturaList);
            }



            LinearLayout ll = findViewById(R.id.ll_dentroscroll);
            System.out.println("-->"+asignaturaList);




            for (String a : asignaturaList) {
                CheckBox opcion = new CheckBox(this);
                opcion.setEnabled(false);
                opcion.setText(a);

                opcion.setLayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                //Seleccionar si ya la tengo



                System.out.println(".."+Build.VERSION.SDK_INT);
                System.out.println(".."+Build.VERSION_CODES.N);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    alumnoController.findByUOWithPhoto(email, new AlumnoController.AlumnoCallback() {
                        @Override
                        public void callback(Alumno alumno) {
                            System.out.println("aa"+alumno);
                            if (alumno != null) {


                                List<String> asignaturasAlumno = crearAsignaturas(alumno.getAsignaturasDominadas());


                                System.out.println("Las de el**"+ asignaturasAlumno);
                                System.out.println("Todas**"+asignaturaList);

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
            String linea = nombre.toString();
            System.out.println("pa" + nombre);
            // a.curso=linea.split("curso=")[1].split(Pattern.quote("}")+",")[0]+"}";
            //a.materia=linea.split("materia=")[1].split(Pattern.quote("}")+",")[0]+"}";
            //a.nombre=linea.split("nombre=")[1].split(",")[0].split(Pattern.quote("}"))[0];
            //esta nal el id

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