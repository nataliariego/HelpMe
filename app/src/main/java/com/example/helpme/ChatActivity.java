package com.example.helpme;

import static com.example.helpme.extras.IntentExtras.CHAT_SELECCIONADO;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Chat;
import com.example.helpme.model.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MensajeAdapter;
import chat.ChatService;
import dto.AlumnoDto;
import dto.ChatSummaryDto;
import dto.MensajeDto;
import util.DateUtils;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "CHAT_ACTIVITY";

    private EditText txMensajeAEnviar;
    private ImageButton btEnviarMensaje;

    private ImageView imgPerfilUsuarioReceiver;
    private TextView txNombreUsuarioReceiver;

    private RecyclerView recyclerConversacionChat;

    private DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore dbStore = FirebaseFirestore.getInstance();

    private AlumnoDto alumnoB = new AlumnoDto();

    private ChatSummaryDto originChatDataDto;

    private MensajeAdapter msgAdapter;
    private List<MensajeDto> chatMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if (savedInstanceState == null) {
            Bundle info = getIntent().getExtras();
            processIntentExtras(info);

        } else {

            //AlumnoDto alumno = (AlumnoDto) savedInstanceState.getSerializable(ListadoAlumnosChatActivity.ALUMNO_SELECCIONADO_CHAT);
            //Log.i(TAG, "ALUMNO-DTO-SAVED-INSTANCE-NN: " + alumno.toString());
        }


        initFields();

        btEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Enviando mensaje...");
                String receiverUid = userInSession.getUid() != "e36oOGlIZlZTGUiYfuU6lng4poo2" ? "nxg2Y3cVaUSf5RAu8R0IaYjkUoZ2" : "e36oOGlIZlZTGUiYfuU6lng4poo2";

                MensajeDto newMsgDto = new MensajeDto();
                newMsgDto.contenido = txMensajeAEnviar.getText().toString();
                newMsgDto.fechaEnvio = LocalDateTime.now();

                ChatService.getInstance().sendMessage(newMsgDto, originChatDataDto);
            }
        });



        if (originChatDataDto != null) {
            /* Mostrar img perfil y nombre del alumnoB */
            paintReceiverData();
        }

        msgAdapter = new MensajeAdapter(chatMessages);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerConversacionChat.setLayoutManager(layoutManager);

        recyclerConversacionChat.setAdapter(msgAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Mostrar los mensajes */
        if (originChatDataDto != null &&
                originChatDataDto.messages != null) {
            paintChatMessages();
            Log.d(TAG, "Número chats: " + chatMessages.size());
        }
    }

    /**
     * Procesa el contenido del intent origen.
     * El intent puede ser original de ListarChatsActivity o de ListadoAlumnosChatActivity
     *
     * @param bundle Bundle que contiene la información de origen.
     */
    private void processIntentExtras(Bundle bundle) {
        /* Si la procedencia del intent es del Activity listado de chats */
        if (bundle == null) {
            Log.e(TAG, "El bundle está vacío.");
            return;
        }

        originChatDataDto = (ChatSummaryDto) bundle.get(CHAT_SELECCIONADO);
        Log.d(TAG, "CHAT_ENTRADA: " + originChatDataDto.chatId + " " + originChatDataDto.receiverProfileImage);
    }

    /**
     * Muestra los datos del usuario receiver del chat en el activity.
     */
    private void paintReceiverData() {
        String name = originChatDataDto.receiverName;
        String imgUrl = originChatDataDto.receiverProfileImage;

        txNombreUsuarioReceiver.setText(name);
        Picasso.get().load(imgUrl).into(imgPerfilUsuarioReceiver);
    }

    /**
     * Muestra los mensajes del chat, actualizando el contenido dinámicamente en función del comportamiento
     * de la lista de mensajes de este (Si se añaden nuevos mensajes por ejemplo).
     * <p>
     * Los mensajes están contenidos dentro del recyclerView recyclerConversacionChat
     */
    private void paintChatMessages() {
        dbReference.child(Chat.REFERENCE)
                .child(originChatDataDto.chatId)
                .child(Mensaje.REFERENCE).addValueEventListener(new ValueEventListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMessages.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Log.i(TAG, "CONTENIDO MSG: " + ds.getValue());

                                Map<String, Object> resData = ((HashMap<String, Object>) ds.getValue());

                                MensajeDto newMessage = new MensajeDto();

                                String content = resData.get(Mensaje.CONTENT).toString();
                                LocalDateTime createdAt = DateUtils.convertHashMapToLocalDateTime((HashMap<String, Object>) resData.get(Mensaje.CREATED_AT));

                                Log.d(TAG, "MENSAJE CHAT: " + content + " " + createdAt.getHour());

                                newMessage.contenido = content;
                                newMessage.fechaEnvio = createdAt;
                                newMessage.userUid = resData.get(Mensaje.SENDER).toString();

                                chatMessages.add(newMessage);

                                msgAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "PAIN_MESSAGES -- CANCELADO. " + error.getMessage());
                    }
                });
    }

    /**
     * Inicializa los campos del activity.
     */
    private void initFields() {
        btEnviarMensaje = (ImageButton) findViewById(R.id.button_enviar_mensaje_chat);
        txMensajeAEnviar = (EditText) findViewById(R.id.text_mensaje_enviar_chat);
        recyclerConversacionChat = (RecyclerView) findViewById(R.id.recycler_conversacion_chat);
        imgPerfilUsuarioReceiver = (ImageView) findViewById(R.id.img_receiver_user_chat);
        txNombreUsuarioReceiver = (TextView) findViewById(R.id.text_user_receiver_chat);


        /* Completar dinámicamente la imagen de perfil y el nombre del alumno receiver */
        txNombreUsuarioReceiver.setText(alumnoB.nombre);
        Picasso.get().load(alumnoB.urlFoto).into(imgPerfilUsuarioReceiver);

    }
}