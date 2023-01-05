package com.example.helpme;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView tvEditarImagen;
    private String urlFoto;

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
        tvEditarImagen = findViewById(R.id.tveditarimagen);


        alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
            @Override
            public void callback(Alumno alumno) {
                if (alumno != null) {
                    textViewUO.setText(alumno.getNombre());
                    textViewEmail.setText(alumno.getEmail());
                    nombreCompleto.setText(alumno.getUo());
                    if (alumno.getUrl_foto() != null && alumno.getUrl_foto() != "")
                        Picasso.get().load(alumno.getUrl_foto()).into(img_persona);
                }
            }
        });

        cargarAsignaturasDominadas();
        // Navegacion
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

        //Boton editar imagen perfil
        tvEditarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarImagenPerfil(view);
            }
        });

        //Si hago click fuera del pop up
        findViewById(R.id.containerView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.containerView).setBackgroundColor(Color.WHITE);
                findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                img_persona.setBackgroundColor(Color.WHITE);
                tvEditarImagen.setBackgroundColor(Color.WHITE);
                findViewById(R.id.buttonVerAmigos).setEnabled(true);
                navegacion.setVisibility(View.VISIBLE);
            }
        });

    }

    private void editarImagenPerfil(View v) {
        findViewById(R.id.containerView).setBackgroundColor(Color.GRAY);
        findViewById(R.id.constraintLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.img_persona_duda).setBackgroundColor(Color.GRAY);
        findViewById(R.id.tveditarimagen).setBackgroundColor(Color.GRAY);
        findViewById(R.id.buttonVerAmigos).setEnabled(false);
        navegacion.setVisibility(View.INVISIBLE);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Introduzca la URL de su nueva imagen de perfil");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setHint(R.string.urlexample);
        alert.setView(input);

        alert.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Encender tdo otra vez
                findViewById(R.id.containerView).setBackgroundColor(Color.WHITE);
                findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                img_persona.setBackgroundColor(Color.WHITE);
                tvEditarImagen.setBackgroundColor(Color.WHITE);
                findViewById(R.id.buttonVerAmigos).setEnabled(true);
                navegacion.setVisibility(View.VISIBLE);
                // Do something with value!
                if (input.getText().toString()!=null
                    && input.getText().toString()!="") {
                    urlFoto = input.getText().toString();
                    Picasso.get().load(input.getText().toString()).into(img_persona);
                }
            }
        });

        alert.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Encender tdo otra vez
                findViewById(R.id.containerView).setBackgroundColor(Color.WHITE);
                findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                img_persona.setBackgroundColor(Color.WHITE);
                tvEditarImagen.setBackgroundColor(Color.WHITE);
                findViewById(R.id.buttonVerAmigos).setEnabled(true);
                navegacion.setVisibility(View.VISIBLE);
                // Canceled.
            }
        });

        alert.show();


    }

    private void ventanaAmigos() {
        Intent listadoDudasIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

                        if (urlFoto!=null && urlFoto!="") {
                            docData.put(Alumno.URL_FOTO, urlFoto);
                        }
                        else {
                            docData.put(Alumno.URL_FOTO, alumno.getUrl_foto());
                        }

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
                            mapAsi.put(String.valueOf(i++), as);
                        }

                        docData.put(Alumno.ASIGNATURAS_DOMINADAS, mapAsi);

                        db.collection(Alumno.COLLECTION).document(alumno.getId()).update(docData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "Perfil actualizado correctamente";
                                            int duration = Toast.LENGTH_SHORT;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
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

}