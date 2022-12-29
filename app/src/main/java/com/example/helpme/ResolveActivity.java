package com.example.helpme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Duda;
import com.example.helpme.model.RespuestaDuda;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
import chat.ChatService;
import controller.AlumnoController;
import dto.AlumnoDto;
import dto.DudaDto;
import dto.RespuestaDto;
import viewmodel.AlumnoViewModel;
import viewmodel.DudaViewModel;
import viewmodel.RespuestaViewModel;

public class ResolveActivity extends AppCompatActivity {

    public static final String CLOUD_STORAGE_URL = "gs://helpme-app-435b7.appspot.com/";
    public static final String BASE_PATH_CLOUD_STORAGE = "imgDudas";
    public static final String DB_URL = "https://helpme-app-435b7-default-rtdb.europe-west1.firebasedatabase.app";

    private Duda duda;
    private TextView titulo;
    private TextView descripcion;
    private TextView numrespuestas;
    private Button botonResolver;
    private Button btonDescargar;
    private EditText respuesta;
    private ImageView imagen;
    private FirebaseFirestore myFirebase;
    private RespuestaAdapter respuestasAdapter;
    private ViewGroup.LayoutParams paramsBefore;
    private AlumnoController alumnoController = new AlumnoController();
    private RespuestaViewModel respuestaViewModel = new RespuestaViewModel();

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();
    private String emailDuda;
    private RecyclerView listadoRespuestas;
    private List<RespuestaDto> respuestas = new ArrayList<>();
    FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);
    private FirebaseStorage cloudStorage = FirebaseStorage.getInstance(CLOUD_STORAGE_URL);
    private StorageReference storageRef = cloudStorage.getReference();
    private StorageReference imgStorage = storageRef.child(BASE_PATH_CLOUD_STORAGE);

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
        imagen = findViewById(R.id.imageViewMostarr);
        titulo.setText(duda.getTitulo());
        descripcion.setText(duda.getDescripcion());
        emailDuda= duda.getEmailAl();
        System.out.println(duda.getUrl_adjunto());
        listadoRespuestas = (RecyclerView) findViewById(R.id.recyclerRespuestas);




        botonResolver = (Button) findViewById(R.id.btnResolver);
        btonDescargar = findViewById(R.id.buttonDescragar);
        if (emailDuda.equals(userInSession.getEmail())){
            botonResolver.setEnabled(false);
            respuesta.setEnabled(false);
            btonDescargar.setEnabled(false);
        }else{
            botonResolver.setEnabled(true);
            respuesta.setEnabled(true);
            btonDescargar.setEnabled(true);
        }

        paramsBefore=respuesta.getLayoutParams();
        long MAX_BYTES = (long) Math.pow(1024, 10);


        if (!duda.getUrl_adjunto().equals("")) {
            imgStorage.child(duda.getUrl_adjunto())
                    .getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d(TAG, "Imagen cargada!");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imagen.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600, 900, false));

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "Error al cargar la imagen del mensaje");
                        }
                    });

        }
        //Cuando hago click fuera de la imagen

        findViewById(R.id.containerView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imagen.setLayoutParams(paramsBefore);
                findViewById(R.id.containerView).setBackgroundColor(Color.WHITE);
                imagen.setVisibility(View.INVISIBLE);
                findViewById(R.id.layout_listar_dudas_wrapper_resolve).setVisibility(View.VISIBLE);
                respuesta.setVisibility(View.VISIBLE);
                // navegacion.setEnabled(false);
            }
        });

        //Cuando hago click dentro de la imagen
        btonDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!duda.getUrl_adjunto().equals("")) {
                    //imagen.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    findViewById(R.id.containerView).setBackgroundColor(Color.GRAY);
                    imagen.setVisibility(View.VISIBLE);
                    respuesta.setVisibility(View.INVISIBLE);
                    //findViewById(R.id.layout_listar_dudas_wrapper_resolve).setVisibility(View.INVISIBLE);
                }else{
                    Snackbar.make(findViewById(R.id.layaoutResolverDuda), R.string.nohayimagen, Snackbar.LENGTH_LONG).show();
                }

            }
        });



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
        int min = 0;
        if (c.get(Calendar.MINUTE) < 10){
            min = 0+c.get(Calendar.MINUTE);
        }
        fecha = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
                + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + min + ":00";
        return fecha;
    }

    private boolean validarCampos() {
        if (respuesta.getText().toString().equals("")) {
            return false;
        }
        return true;
    }


}