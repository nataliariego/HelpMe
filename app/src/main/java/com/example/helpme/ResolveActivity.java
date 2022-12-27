package com.example.helpme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Duda;
import com.example.helpme.model.RespuestaDuda;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import adapter.AlumnoAdapter;
import adapter.DudaAdapter;
import adapter.RespuestaAdapter;
import assembler.CursoAssembler;
import assembler.MateriaAssembler;
import controller.AlumnoController;
import dto.AlumnoDto;
import dto.DudaDto;
import dto.RespuestaDto;
import viewmodel.AlumnoViewModel;
import viewmodel.DudaViewModel;
import viewmodel.RespuestaViewModel;

public class ResolveActivity extends AppCompatActivity {

    private Duda duda;
    private TextView titulo;
    private TextView descripcion;
    private TextView numrespuestas;
    private Button botonResolver;
    private EditText respuesta;
    private FirebaseFirestore myFirebase;
    private RespuestaAdapter respuestasAdapter;
    private AlumnoController alumnoController = new AlumnoController();
    private RespuestaViewModel respuestaViewModel = new RespuestaViewModel();

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();
    private String emailDuda;
    private RecyclerView listadoRespuestas;
    private List<RespuestaDto> respuestas = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve);

        myFirebase = FirebaseFirestore.getInstance();
        Intent intentDuda= getIntent();
        duda= intentDuda.getParcelableExtra(ListarDudasActivity.DUDA_SELECCIONADA);
        if (duda == null){
            duda= intentDuda.getParcelableExtra(HomeActivity.DUDA_SELECCIONADA);
        }

        titulo = findViewById(R.id.tituloDuda);
        descripcion = findViewById(R.id.textDescripcion);
        respuesta = findViewById(R.id.editTextRespuesta);
        numrespuestas = findViewById(R.id.textRespuestas);

        titulo.setText(duda.getTitulo());
        descripcion.setText(duda.getDescripcion());
        emailDuda= duda.getEmailAl();

        listadoRespuestas = (RecyclerView) findViewById(R.id.recyclerRespuestas);




        botonResolver = (Button) findViewById(R.id.btnResolver);
        if (emailDuda.equals(userInSession.getEmail())){
            botonResolver.setEnabled(false);
            respuesta.setEnabled(false);
        }else{
            botonResolver.setEnabled(true);
            respuesta.setEnabled(true);
        }



        botonResolver.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    publicarDuda();
                    Snackbar.make(findViewById(R.id.layaoutResolverDuda), R.string.respuestaEnviada, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(findViewById(R.id.layaoutResolverDuda), R.string.camposRellenados, Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        listadoRespuestas.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listadoRespuestas.setLayoutManager(layoutManager);

        cargarRespuestas(duda.getId());

        respuestasAdapter = new RespuestaAdapter(respuestas);
        listadoRespuestas.setAdapter(respuestasAdapter);
        respuestasAdapter.notifyDataSetChanged();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarRespuestas(String idDuda) {
        respuestas.clear();
        respuestaViewModel.getAllRespuestas().observe(this, respuestasResult -> {
            if (respuestasResult != null) {
                respuestasResult.forEach(d -> {
                    respuestas.clear();
                    alumnoController.findByUOWithPhoto(d.getEmailResponde(), new AlumnoController.AlumnoCallback() {
                                @Override
                                public void callback(Alumno alumno) {
                                    if (d.getIdDuda().equals(idDuda)) {
                                        RespuestaDto newRes = new RespuestaDto();
                                        newRes.id = d.getId();
                                        newRes.alumnoDuda=d.getEmailDuda();
                                        newRes.alumnoResponde=d.getEmailResponde();
                                        newRes.fecha=d.getFecha();
                                        newRes.respuesta=d.getDescripcion();
                                        newRes.idDuda=d.getIdDuda();
                                        newRes.nombreAlumnoResponde= alumno.getUo();
                                        newRes.url_foto_responde=alumno.getUrl_foto();

                                        respuestas.add(newRes);
                                    }
                                    numrespuestas.setText("Todas las respuestas ("+respuestas.size()+")");
                                    respuestasAdapter = new RespuestaAdapter(respuestas);
                                    listadoRespuestas.setAdapter(respuestasAdapter);
                                    respuestasAdapter.notifyDataSetChanged();
                    }});

                });
            }



        });

    }

    private void publicarDuda() {

        String fecha = sacarFecha();
        Map<String, Object> docData = new HashMap<>();
        docData.put("alumnoDuda", emailDuda);
        docData.put("alumnoResponde", userInSession.getEmail());
        docData.put("idDuda", duda.getId());
        docData.put("respuesta", respuesta.getText().toString());
        docData.put("fecha", fecha);

        System.out.println(docData);
        myFirebase.collection(RespuestaDuda.COLLECTION).document()
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
        respuesta.setText("");

    }

    private String sacarFecha() {
        String fecha;
        Calendar c = new GregorianCalendar();
        fecha = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
                + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":00";
        return fecha;
    }

    private boolean validarCampos() {
        if (respuesta.getText().toString().equals("")) {
            return false;
        }
        return true;
    }


}