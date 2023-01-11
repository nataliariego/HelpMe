package com.example.helpme;

import static com.example.helpme.extras.IntentExtras.CHAT_SELECCIONADO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.extras.IntentExtras;
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Chat;
import com.example.helpme.model.Mensaje;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.Objects;
import java.util.stream.Collectors;

import adapter.ChatAdapter;
import chat.ChatService;
import dto.ChatSummaryDto;

public class ListarChatsActivity extends AppCompatActivity {
    public static final String TAG = "LISTAR_CHATS_ACTIVITY";
    public static final String CHAT_UIDS = "CHAT_UIDS";

    private final DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private final FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
    private final FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private RecyclerView recyclerListadoChats;
    private FloatingActionButton fabNuevoChat;
    private TextView mensajeNoHayConversaciones;

    private final List<ChatSummaryDto> chats = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private boolean loadingChatsFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_chats);
        setTitle("Chats");

        initFields();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chatAdapter = new ChatAdapter(chats, item -> {
                Intent intent = new Intent(ListarChatsActivity.this, ChatActivity.class);
                intent.putExtra(CHAT_SELECCIONADO, item);
                startActivity(intent);
            });
        }

        recyclerListadoChats.setAdapter(chatAdapter);

        BottomNavigationView navegacion = findViewById(R.id.bottomNavigationView);
        IntentExtras.getInstance().handleNavigationView(navegacion, getBaseContext());

        registerForContextMenu(recyclerListadoChats);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(@NonNull Menu menu) {
        super.onContextMenuClosed(menu);
        Log.d(TAG, "menu cerrado");
        chatAdapter.resetChatStyles();
    }

    private void initFields() {
        recyclerListadoChats = findViewById(R.id.recycler_listado_chats);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerListadoChats.setLayoutManager(layoutManager);
        fabNuevoChat = (FloatingActionButton) findViewById(R.id.fab_nuevo_chat);
        mensajeNoHayConversaciones = (TextView) findViewById(R.id.text_ningun_chat);

        toogleMessage(false);

        addListeners();
    }

    private void addListeners() {
        fabNuevoChat.setOnClickListener(view -> {
            Intent intent = new Intent(ListarChatsActivity.this, ListadoAlumnosChatActivity.class);

            if (chats.size() > 0) {
                List<String> chatUids = chats.stream().map(chat -> chat.receiverUid).collect(Collectors.toList());
                intent.putStringArrayListExtra(CHAT_UIDS, (ArrayList<String>) chatUids);
            }

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarChats();
    }

    private void cargarChats() {
        loadingChatsFinished = false;
        dbReference.child(Chat.REFERENCE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chats.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (((HashMap<String, Object>) Objects.requireNonNull(ds.getValue())).get(Mensaje.REFERENCE) != null) {
                                    ChatSummaryDto summary = new ChatSummaryDto();
                                    summary.chatId = ds.getKey();
                                    String uidAlumnoA = Objects.requireNonNull(((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_A)).toString();
                                    String uidAlumnoB = Objects.requireNonNull(((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_B)).toString();

                                    assert userInSession != null;
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
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot res = task.getResult();

                                                        Log.d(TAG, res.toString());

                                                        String nombre = Objects.requireNonNull(res.get(Alumno.NOMBRE)).toString();

                                                        String urlFoto = res.get(Alumno.URL_FOTO) != null
                                                                ? Objects.requireNonNull(res.get(Alumno.URL_FOTO)).toString()
                                                                : "https://ui-avatars.com/api/?name=" + String.join(nombre);

                                                        summary.receiverProfileImage = urlFoto;
                                                        summary.receiverName = nombre;
                                                        summary.receiverUid = otherUserUid;

                                                        chats.add(summary);

                                                        chatAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.d(TAG, "Error al cargar los chats.\n" + e.getMessage());
                                                    e.printStackTrace();
                                                });
                                    }else{
                                        Log.d(TAG, "Num chats: "+ chats.size());
                                        toogleMessage(chats.isEmpty());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //toogleMessage(true);
                        Log.e(TAG, "Error al cargar chats. " + error.getMessage());
                    }
                });
    }

    /**
     * Si el usuario en sesión no tiene conversaciones, se mostrará el mensaje correspondiente.
     *
     * @param show true para mostrar el mensaje y false para ocultarlo.
     */
    private void toogleMessage(final boolean show) {

        if (show) {
            recyclerListadoChats.setVisibility(View.GONE);
            mensajeNoHayConversaciones.setVisibility(View.VISIBLE);
        } else {
            mensajeNoHayConversaciones.setVisibility(View.GONE);
            recyclerListadoChats.setVisibility(View.VISIBLE);
        }

    }
}