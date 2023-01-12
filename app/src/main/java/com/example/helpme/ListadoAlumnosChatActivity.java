package com.example.helpme;

import static com.example.helpme.extras.IntentExtras.CHAT_SELECCIONADO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Chat;
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
import java.util.Objects;
import java.util.UUID;

import adapter.AlumnoChatAdapter;
import chat.AlumnoStatus;
import chat.ChatService;
import dto.AlumnoDto;
import dto.ChatSummaryDto;
import viewmodel.AlumnoViewModel;

public class ListadoAlumnosChatActivity extends AppCompatActivity {

    public static final String TAG = "LIST_ALUM_CHAT_ACTIVITY";

    private final AlumnoViewModel alumnoViewModel = new AlumnoViewModel();
    private final DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    @SuppressLint("StaticFieldLeak")
    private static final FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
    private final FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView recyclerAlumnosChat;

    private AlumnoChatAdapter alumnoChatAdapter;
    private final List<AlumnoDto> alumnos = new ArrayList<>();
    private List<String> currentUserChatsUids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_alumnos_chat);
        setTitle("Alumnos disponibles en el chat");

        initFields();

        /* Chats del usuario, si tiene */
        currentUserChatsUids = getIntent().hasExtra(ListarChatsActivity.CHAT_UIDS)
                && getIntent().getStringArrayListExtra(ListarChatsActivity.CHAT_UIDS) != null
                ? getIntent().getStringArrayListExtra(ListarChatsActivity.CHAT_UIDS)
                : new ArrayList<>();

    }

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

    /**
     * Carga todos los alumnos disponibles en la aplicacion.
     */
    private void cargarAlumnos() {
        String email = userInSession.getEmail();
        alumnoViewModel.getAllAlumnosFriendsActivity().observe(this, alumnosResult -> {
            alumnos.clear();
            if (alumnosResult != null) {
                alumnosResult.forEach(d -> {
                    if (!d.getEmail().equalsIgnoreCase(email)
                            && !currentUserChatsUids.contains(d.getId())) {
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

            alumnoChatAdapter = new AlumnoChatAdapter(alumnos, alumno -> {
                String chatUUID = UUID.randomUUID().toString();
                Map<String, Object> payload = new HashMap<>();

                payload.put(Chat.ALUMNO_A, userInSession.getUid());

                Task<QuerySnapshot> alumnoBData = dbStore.collection(Alumno.COLLECTION).whereEqualTo(Alumno.EMAIL, alumno.email).get();

                alumnoBData.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                List<DocumentSnapshot> res = task.getResult().getDocuments();

                                if (res.size() > 0) {
                                    DocumentSnapshot doc = res.get(0);

                                    String uidAlumnoB = doc.getId();

                                    payload.put(Chat.ALUMNO_B, uidAlumnoB);
                                    dbReference.child(Chat.REFERENCE).child(chatUUID).updateChildren(payload);

                                    /* Contenido que recibirá el chat creado */
                                    ChatSummaryDto summary = new ChatSummaryDto();
                                    summary.chatId = chatUUID;
                                    summary.receiverName = Objects.requireNonNull(doc.get(Alumno.NOMBRE)).toString();

                                    if (doc.get(Alumno.URL_FOTO) != null) {
                                        summary.receiverProfileImage = Objects.requireNonNull(doc.get(Alumno.URL_FOTO)).toString();
                                    }

                                    if (!dbReference.child(Alumno.REFERENCE).child(uidAlumnoB).get().isSuccessful()) {

                                        Map<String, Object> payloadAlumno = new HashMap<>();
                                        payloadAlumno.put(Alumno.NOMBRE, Objects.requireNonNull(doc.get(Alumno.NOMBRE)).toString());
                                        payloadAlumno.put(Alumno.STATUS, AlumnoStatus.OFFLINE.toString().toLowerCase());

                                        dbReference.child(Alumno.REFERENCE).child(uidAlumnoB).updateChildren(payloadAlumno).addOnSuccessListener(unused -> {
                                                    summary.receiverUid = doc.getId();

                                                    Intent intent = new Intent(ListadoAlumnosChatActivity.this, ChatActivity.class);
                                                    intent.putExtra(CHAT_SELECCIONADO, summary);
                                                    startActivity(intent);
                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "No se ha podido registrar el alumno. " + e.getMessage());
                                                    dbReference.child(Chat.REFERENCE).child(chatUUID).removeValue();
                                                });
                                    } else {
                                        // Alumno ya registrado en la base de datos en tiempo real.
                                        Intent intent = new Intent(ListadoAlumnosChatActivity.this, ChatActivity.class);
                                        intent.putExtra(CHAT_SELECCIONADO, summary);
                                        startActivity(intent);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "Error al buscar el alumno con el email indicado."));
            });
            recyclerAlumnosChat.setAdapter(alumnoChatAdapter);
        });
    }


}