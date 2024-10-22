package com.example.helpme;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Chat;
import com.example.helpme.model.Duda;
import com.example.helpme.model.Mensaje;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.ocpsoft.prettytime.units.Hour;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import assembler.CursoAssembler;
import assembler.MateriaAssembler;
import controller.AlumnoController;
import controller.CursoController;
import dto.AsignaturaDto;
import dto.CursoDto;
import dto.MateriaDto;
import util.DateUtils;
import viewmodel.AsignaturaViewModel;
import viewmodel.CursoViewModel;
import viewmodel.MateriaViewModel;

public class PublicarDudaActivity extends AppCompatActivity {

    public static final String CLOUD_STORAGE_URL = "gs://helpme-app-435b7.appspot.com/";
    public static final String BASE_PATH_CLOUD_STORAGE = "imgDudas";
    public static final String DB_URL = "https://helpme-app-435b7-default-rtdb.europe-west1.firebasedatabase.app";

    private Spinner spinner;
    private EditText titulo;
    private EditText descripcion;
    private ImageButton imagenAdjuntar;
    private TextView adjuntar;
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
    private List<MateriaDto> materias = new ArrayList<>();
    private AlumnoController alumnoController = new AlumnoController();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();
    private Bitmap selectedImageToSend;

    private FirebaseStorage cloudStorage = FirebaseStorage.getInstance(CLOUD_STORAGE_URL);
    private StorageReference storageRef = cloudStorage.getReference();
    private StorageReference imgStorage = storageRef.child(BASE_PATH_CLOUD_STORAGE);


    FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);

    private String url_imagen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_duda);
        imagenAdjuntar = findViewById(R.id.imageButtonAdjuntar);
        spinner = (Spinner) findViewById(R.id.spinnerAsignaturas);
        titulo = (EditText) findViewById(R.id.editTextTituloDudaNueva);
        descripcion = (EditText) findViewById(R.id.editTextDuda);
        adjuntar = findViewById(R.id.textViewAdjuntar);
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



        /* Abre la galería de imágenes del dispositivo */
        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Uri uri = result.getData().getData();
                            System.out.println(uri);
                            try {
                                selectedImageToSend = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "Imagen recibida");

                            ImageView selected = new ImageView(getApplicationContext());
                            selected.setImageBitmap(selectedImageToSend);
                            uploadImage(selected);

                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            Toast.makeText(getApplicationContext(), "Acción cancelada", Toast.LENGTH_SHORT);
                        }


                    }
                }
        );
        imagenAdjuntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "BT-ADJUNTAR ARCHIVO: ");

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");

                galleryLauncher.launch(intent);
            }
        });


        //Navegacion:
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            redirectToLogin();
        }
    }

    private void uploadImage(ImageView imageView) {

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String imgUid = UUID.randomUUID().toString();
        String imageName = imgUid + ".jpg";
        url_imagen = imageName;
        adjuntar.setText("Adjuntar imagen " + "(1)");
        imagenAdjuntar.setEnabled(false);
        UploadTask uploadTask = imgStorage.child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error al subir la imagen a Cloud Storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getTask().isSuccessful()) {
                    Log.d(TAG, "Imagen subida: " + taskSnapshot.getMetadata());
                    Log.d(TAG, "Imagen subida2: " + taskSnapshot.getStorage());
                    Log.d(TAG, "Imagen subida3: " + taskSnapshot.getTask().getResult());
                    Log.d(TAG, "Imagen subida4: " + taskSnapshot.getMetadata().getReference());
                    Log.d(TAG, "Imagen subida5: " + taskSnapshot.getMetadata().getPath());

                    Map<String, Object> payload = new HashMap<>();
                    payload.put(Mensaje.SENDER, userInSession.getUid());
                    payload.put(Mensaje.CONTENT, imgStorage.child(imageName).getPath());
                    payload.put(Mensaje.MESSAGE_TYPE, "image/jpeg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        payload.put(Mensaje.CREATED_AT, DateUtils.getNowWithPredefinedFormat());
                    }


                    db.getReference().child(Chat.REFERENCE).child(Mensaje.REFERENCE).child(imgUid).updateChildren(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "Imagen subida. ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "ERROR al subir la imagen al servidor. " + e.getMessage());
                        }
                    });
                }

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
                newDuda.setFecha(DateUtils.getNowWithPredefinedFormat());
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

        //Pongo los datos del usuario que está autenticado
        String email = userInSession.getEmail();

        //Tengo que buscar el alumno que tenga ese email para poner después los datos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alumnoController.findByUOWithPhoto(userInSession.getEmail(), new AlumnoController.AlumnoCallback() {
                @Override
                public void callback(Alumno alumno) {
                    if (alumno != null) {
                        Map<String, Object> alumnoMap = new HashMap<>();
                        alumnoMap.put(Alumno.NOMBRE, alumno.getUo());
                        alumnoMap.put(Alumno.EMAIL, alumno.getEmail());
                        alumnoMap.put(Alumno.ID, alumno.getId());
                        alumnoMap.put(Alumno.UO, alumno.getNombre());
                        alumnoMap.put(Alumno.URL_FOTO, alumno.getUrl_foto());
                        alumnoMap.put(Alumno.ASIGNATURAS_DOMINADAS, alumno.getAsignaturasDominadas());
                        Map<String, Object> asignaturaMap = new HashMap<>();
                        String nAsignatura = spinner.getSelectedItem().toString();
                        crearAsignaturaDuda(nAsignatura);

                        Map<String, Object> cursoMap = CursoAssembler.toHashMap(asignaturaDuda.get(0).curso);


                        Map<String, Object> materiaMap = MateriaAssembler.toHashMap(asignaturaDuda.get(0).materia);
                        asignaturaMap.put(Asignatura.NOMBRE, nAsignatura);
                        asignaturaMap.put(Asignatura.CURSO, cursoMap);
                        asignaturaMap.put(Asignatura.ID, asignaturaDuda.get(0).id);
                        asignaturaMap.put(Asignatura.MATERIA, materiaMap);
                        asignaturaDuda.clear();


                        Map<String, Object> docData = new HashMap<>();
                        docData.put(Duda.TITULO, titulo.getText().toString());
                        docData.put(Duda.DESCRIPCION, descripcion.getText().toString());
                        docData.put(Duda.REF_ALUMNO, alumnoMap);
                        docData.put(Duda.ASIGNATURA_REF, asignaturaMap);
                        docData.put(Duda.REF_MATERIA, materiaMap);
                        docData.put(Duda.IS_RESUELTA, false);

                        docData.put(Duda.FECHA, String.format(DateUtils.format(fecha)));
                        //docData.put(Duda.FECHA, fecha);
                        docData.put(Duda.URL_ADJUNTO, url_imagen);


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
                        adjuntar.setText("Adjuntar imagen");
                    }
                }
            });
        }


    }

    private String sacarFecha() {
        String fecha;
        Calendar c = new GregorianCalendar();
        String min = "";
        String hour="";
        if (c.get(Calendar.HOUR_OF_DAY) <10){
            hour = "0"+(c.get(Calendar.HOUR_OF_DAY)+1);
        }else{
            hour=""+(c.get(Calendar.HOUR_OF_DAY)+1);
        }
        System.out.println(c.get(Calendar.MINUTE));
        if (c.get(Calendar.MINUTE) < 10){
            min = "0"+c.get(Calendar.MINUTE);
        }else{
            min=""+c.get(Calendar.MINUTE);

        }
        fecha = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
                + " " + hour + ":" + min + ":00";
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

            for (AsignaturaDto dto : asignaturaList
            ) {
                nombreAsignaturas.add(dto.nombre);
            }

            spinner = findViewById(R.id.spinnerAsignaturas);
            spinner.setAdapter(new ArrayAdapter<>(PublicarDudaActivity.this, android.R.layout.simple_selectable_list_item, nombreAsignaturas));

        });
    }


    private void crearAsignaturaDuda(String nombreA) {
        asignaturaViewModel.getAllDudas().observe(this, dudasResult -> {
            if (dudasResult != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dudasResult.forEach(d -> {

                        AsignaturaDto a = new AsignaturaDto();
                        if (d.getNombre().equals(nombreA)) {
                            a.nombre = d.getNombre();
                            a.id = d.getId();
                            a.curso = d.getCurso();
                            a.materia = d.getMateria();
                            asignaturaDuda.add(a);
                        }
                    });
                }
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(PublicarDudaActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}