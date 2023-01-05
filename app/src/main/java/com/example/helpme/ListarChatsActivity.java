package com.example.helpme;

import static com.example.helpme.extras.IntentExtras.CHAT_SELECCIONADO;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Chat;
import com.example.helpme.model.Mensaje;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ChatAdapter;
import chat.ChatService;
import dto.ChatSummaryDto;

public class ListarChatsActivity extends AppCompatActivity {
    public static final String TAG = "LISTAR_CHATS_ACTIVITY";

    private DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private ChatAdapter chatAdapter;

    private RecyclerView recyclerListadoChats;
    private List<ChatSummaryDto> chats = new ArrayList<>();

    private FloatingActionButton fabNuevoChat;
    private BottomNavigationView navegacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_chats);
        setTitle("Chats");

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        initFields();

        Log.i(TAG, "USUARIO EN SESIÓN: " + userInSession.getEmail());

        Log.d(TAG, "version android: " + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chatAdapter = new ChatAdapter(chats, new ChatAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ChatSummaryDto item) {
                    Intent intent = new Intent(ListarChatsActivity.this, ChatActivity.class);
                    intent.putExtra(CHAT_SELECCIONADO, item);
                    startActivity(intent);
                }
            });
        }

        recyclerListadoChats.setAdapter(chatAdapter);

        //Navegación
        navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());

    }

    private void initFields() {
        recyclerListadoChats = findViewById(R.id.recycler_listado_chats);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerListadoChats.setLayoutManager(layoutManager);

        fabNuevoChat = (FloatingActionButton) findViewById(R.id.fab_nuevo_chat);

        fabNuevoChat.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListarChatsActivity.this, ListadoAlumnosChatActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarChats();
    }

    private void cargarChats() {
        dbReference.child(Chat.REFERENCE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chats.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (((HashMap<String, Object>) ds.getValue()).get(Mensaje.REFERENCE) != null) {
                                    ChatSummaryDto summary = new ChatSummaryDto();
                                    summary.chatId = ds.getKey();
                                    String uidAlumnoA = ((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_A).toString();
                                    String uidAlumnoB = ((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_B).toString();

                                    Log.d(TAG, "UserInSession: " + userInSession.getUid() + "Alumno A: " + uidAlumnoA + " Uid Alumno B: " + uidAlumnoB);

                                    /* Mensajes del chat */
                                    if (((HashMap<String, Object>) ds.getValue()).get(Mensaje.REFERENCE) != null) {
                                        summary.messages = (Map<String, Object>) ((HashMap<String, Object>) ds.getValue()).get(Mensaje.REFERENCE);
                                    }

                                    Log.d(TAG, "userInSession: " + userInSession.getUid() + " " + uidAlumnoA + " " + uidAlumnoB);
                                    if (userInSession.getUid().equals(uidAlumnoA)
                                            || userInSession.getUid().equals(uidAlumnoB)) {
                                        String otherUserUid = uidAlumnoB.equals(userInSession.getUid()) ? uidAlumnoA : uidAlumnoB;

                                        dbStore.collection(Alumno.COLLECTION)
                                                .document(otherUserUid)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            DocumentSnapshot res = task.getResult();

                                                            Log.d(TAG, res.toString());

                                                            String nombre = res.get(Alumno.NOMBRE).toString();

                                                            String urlFoto = res.get(Alumno.URL_FOTO) != null
                                                                    ? res.get(Alumno.URL_FOTO).toString()
                                                                    : "https://ui-avatars.com/api/?name=" + String.join(nombre);

                                                            summary.receiverProfileImage = urlFoto;
                                                            summary.receiverName = nombre;
                                                            summary.receiverUid = otherUserUid;


                                                            Log.d(TAG, "summary: " + summary.receiverName);

                                                            chats.add(summary);

                                                            chatAdapter.notifyDataSetChanged();


                                                            Log.d(TAG, nombre + " -- " + urlFoto);
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "Error al cargar los chats.\n" + e.getMessage());
                                                        e.printStackTrace();
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}