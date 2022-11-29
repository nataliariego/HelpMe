package com.example.helpme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import adapter.AlumnoChatAdapter;
import chat.ChatService;
import dto.AlumnoDto;
import viewmodel.AlumnoViewModel;

public class ListadoAlumnosChatActivity extends AppCompatActivity {

    public static final String TAG = "LIST_ALUM_CHAT_ACTIVITY";
    public static final String ALUMNO_SELECCIONADO_CHAT = "alumno_seleccionado_chat";

    private AlumnoViewModel alumnoViewModel = new AlumnoViewModel();

    private DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private static FirebaseFirestore dbStore = FirebaseFirestore.getInstance();

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView recyclerAlumnosChat;

    private List<AlumnoDto> alumnos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_alumnos_chat);
        setTitle("Alumnos disponibles en el chat");

        initFields();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        recyclerAlumnosChat.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAlumnosChat.setLayoutManager(layoutManager);

        cargarAlumnos();
    }


    private void initFields() {
        recyclerAlumnosChat = (RecyclerView) findViewById(R.id.recycler_listado_alumnos_chat);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarAlumnos() {
        String email = userInSession.getEmail();
        alumnoViewModel.getAllAlumnosFriendsActivity().observe(this, alumnosResult -> {
            alumnos.clear();
            if (alumnosResult != null) {
                alumnosResult.forEach(d -> {
                    if (!d.getEmail().equalsIgnoreCase(email)) {
                        Log.i(TAG, d.getNombre() + " " + d.getUo() + " " + d.getUrl_foto());
                        AlumnoDto alumno = new AlumnoDto();
                        alumno.nombre = d.getNombre();
                        alumno.uo = d.getUo();
                        alumno.urlFoto = d.getUrl_foto();
                        alumno.asignaturasDominadas = d.getAsignaturasDominadas();
                        alumno.email = d.getEmail();
                        alumno.id = d.getId();

                        alumnos.add(alumno);
                    }
                });
            }

            AlumnoChatAdapter adapter = new AlumnoChatAdapter(alumnos, new AlumnoChatAdapter.OnClickListener() {
                @Override
                public void goToChat(AlumnoDto alumno) {
                    // TODO: Crear registro chat con id en realtime database
                    String chatUUID = UUID.randomUUID().toString();

                    Map<String, Object> payload = new HashMap<>();

                    payload.put("alumnoA", userInSession.getUid());

                    Task<QuerySnapshot> alumnoBData = dbStore.collection(Alumno.COLLECTION).whereEqualTo(Alumno.EMAIL, alumno.email).get();

                    alumnoBData.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        List<DocumentSnapshot> res = task.getResult().getDocuments();

                                        if (res.size() > 0) {

                                            String uidAlumnoB = res.get(0).getId();

                                            payload.put("alumnoB", uidAlumnoB);
                                            //payload.put("messages", new HashMap<>());
                                            //dbReference.child(Chat.REFERENCE).child(chatUUID).updateChildren(payload);

                                            Intent intent = new Intent(ListadoAlumnosChatActivity.this, ChatActivity.class);
                                            intent.putExtra(ALUMNO_SELECCIONADO_CHAT, alumno);
                                            startActivity(intent);

                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error al buscar el alumno con el email indicado.");
                                }
                            });
                }
            });
            recyclerAlumnosChat.setAdapter(adapter);
        });
    }
}