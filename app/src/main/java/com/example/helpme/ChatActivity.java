package com.example.helpme;

import static com.example.helpme.extras.IntentExtras.CHAT_SELECCIONADO;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    /* Identicar intent que accede a la cámara del dispositivo */
    public static final int CAMERA_IMAGE_CHAT = 1000;

    private EditText txMensajeAEnviar;
    private ImageButton btEnviarMensaje;

    private ImageView imgPerfilUsuarioReceiver;
    private TextView txNombreUsuarioReceiver;

    private ImageButton btSubirArchivoChat;
    private ImageButton btVolverListaChats;
    private ImageButton btLlamarReceiver;
    private ImageButton btCamera;

    private RecyclerView recyclerConversacionChat;

    private DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore dbStore = FirebaseFirestore.getInstance();

    private AlumnoDto alumnoB = new AlumnoDto();

    private ChatSummaryDto originChatDataDto;

    private MensajeAdapter msgAdapter;
    private List<MensajeDto> chatMessages = new ArrayList<>();

    private Bitmap selectedImageToSend;

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

        /* Accion enviar mensaje */
        btEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Enviando mensaje...");

                MensajeDto newMsgDto = new MensajeDto();
                newMsgDto.contenido = txMensajeAEnviar.getText().toString();
                newMsgDto.createdAt = DateUtils.getNowWithPredefinedFormat();

                ChatService.getInstance().sendMessage(newMsgDto, originChatDataDto, new ChatService.MensajeCallback() {
                    @Override
                    public void callback() {
                        txMensajeAEnviar.setText("");
                    }
                });
            }
        });

        /* Accion seleccionar archivo para enviar */
        btSubirArchivoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "BT-ADJUNTAR ARCHIVO: ");
            }
        });

        /* Accion volver a la lista de chats */
        btVolverListaChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, ListarChatsActivity.class));
            }
        });

        /* Accion llamar al alumno del chat */
        btLlamarReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (originChatDataDto != null) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    //userInSession.getEmail()
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"kikocoya@gmail.com"});
                    email.putExtra(Intent.EXTRA_SUBJECT, "HelpMe App - Un compañero necesita tu ayuda.");
                    email.putExtra(Intent.EXTRA_TEXT, "Hola, soy " + userInSession.getDisplayName() + "\n\n\nHelpMe App");

                    email.setType("text/plain");

                    startActivity(Intent.createChooser(email, "Enviando correo..."));
                }
            }
        });

        /* Accion sacar foto para enviar por chat */
        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "CAMERA: ");

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_IMAGE_CHAT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* Imagen tomada de la cámara */
        if (requestCode == CAMERA_IMAGE_CHAT && resultCode == Activity.RESULT_OK) {
            selectedImageToSend = (Bitmap) data.getExtras().get("data");
            Log.d(TAG, "Imagen recibida");

            ImageView selected = new ImageView(getApplicationContext());
            selected.setImageBitmap(selectedImageToSend);
            uploadImage(selected);
        }
    }

    private void uploadImage(ImageView imageView) {
        if (originChatDataDto == null) {
            return;
        }

        ChatService.getInstance().uploadImage(imageView, originChatDataDto, new ChatService.MensajeCallback() {
            @Override
            public void callback() {
                Log.d(TAG, "Imagen subida al servidor!");
                msgAdapter.notifyDataSetChanged();
            }
        });
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
                .child(Mensaje.REFERENCE)
                .addValueEventListener(new ValueEventListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMessages.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Map<String, Object> resData = ((HashMap<String, Object>) ds.getValue());

                                MensajeDto newMessage = new MensajeDto();

                                String content = resData.get(Mensaje.CONTENT).toString();
                                String createdAt = resData.get(Mensaje.CREATED_AT).toString();

                                newMessage.contenido = content;
                                newMessage.createdAt = createdAt;
                                newMessage.mimeType = resData.get(Mensaje.MESSAGE_TYPE).toString();
                                newMessage.userUid = resData.get(Mensaje.SENDER).toString();

                                chatMessages.add(newMessage);

                            }
                            msgAdapter.sortMessages();
                            for(MensajeDto msg : chatMessages){
                                Log.d(TAG, "MSG LOADED: " + msg.contenido);
                            }
                            //msgAdapter.notifyDataSetChanged();
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
        btSubirArchivoChat = (ImageButton) findViewById(R.id.button_upload_file_chat);
        btVolverListaChats = (ImageButton) findViewById(R.id.button_back_to_chat_list);
        btLlamarReceiver = (ImageButton) findViewById(R.id.button_call_alumno_receiver);
        btCamera = (ImageButton) findViewById(R.id.button_camera_chat);

        /* Completar dinámicamente la imagen de perfil y el nombre del alumno receiver */
        txNombreUsuarioReceiver.setText(alumnoB.nombre);
        Picasso.get().load(alumnoB.urlFoto).into(imgPerfilUsuarioReceiver);

    }
}