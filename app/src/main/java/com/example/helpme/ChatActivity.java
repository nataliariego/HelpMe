package com.example.helpme;

import static com.example.helpme.extras.IntentExtras.CHAT_SELECCIONADO;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import adapter.MensajeAdapter;
import chat.AlumnoStatus;
import chat.ChatService;
import chat.MensajeStatus;
import dto.AlumnoDto;
import dto.ChatSummaryDto;
import dto.MensajeDto;
import util.ContentTypeUtils;
import util.DateUtils;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "CHAT_ACTIVITY";
    public static final String FILE_LAUNCHER = "fileLauncher";
    public static final String GALLERY_LAUNCHER = "galleryLauncher";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1002;

    private EditText txMensajeAEnviar;
    private ImageButton btEnviarMensaje;

    private ImageView imgPerfilUsuarioReceiver;
    private TextView txNombreUsuarioReceiver;

    private ImageButton btSubirArchivoChat;
    private ImageButton btVolverListaChats;
    private ImageButton btLlamarReceiver;
    private ImageButton btCamera;

    private RecyclerView recyclerConversacionChat;

    private final DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private final FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private final AlumnoDto alumnoB = new AlumnoDto();

    private ChatSummaryDto originChatDataDto;

    private MensajeAdapter msgAdapter;
    private final List<MensajeDto> chatMessages = new ArrayList<>();

    private Bitmap selectedImageToSend;

    /* Permisos grabar audio */
    private boolean permissionToRecordAccepted = false;

    /* Online, Offline, Escribiendo...*/
    private static final String ONLINE_STATUS = AlumnoStatus.ONLINE.toString().toLowerCase();
    private static final String TYPING_STATUS = AlumnoStatus.ESCRIBIENDO.toString().toLowerCase();
    private String status = ONLINE_STATUS;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if (savedInstanceState == null && userInSession != null) {
            Bundle info = getIntent().getExtras();
            processIntentExtras(info);

        } else {
            Toast.makeText(this, R.string.inicia_sesion, Toast.LENGTH_SHORT).show();
        }

        initFields();
        addListeners();

        if (originChatDataDto != null) {
            /* Mostrar img perfil y nombre del alumnoB */
            paintReceiverData();

            if (originChatDataDto.messages != null) {
                paintChatMessages();
            }
        }

        msgAdapter = new MensajeAdapter(chatMessages);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerConversacionChat.setLayoutManager(layoutManager);

        recyclerConversacionChat.setAdapter(msgAdapter);

    }

    private Map<String, ActivityResultLauncher> configureLaunchers() {

        Map<String, ActivityResultLauncher> launchers = new HashMap();

        /* Abre el explorador de archivos del dispositivo */
        ActivityResultLauncher<Intent> fileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        /* Documento o imagen seleccionado del dispositivo */
                        Log.d(TAG, "Documento recibido...");
                        Log.d(TAG, result.getData().toString());
                        Uri selectedMediaUri = result.getData().getData();
                        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(selectedMediaUri, null, null, null, null);
                        int filenameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                        cursor.moveToFirst();

                        String filename = cursor.getString(filenameIndex);

                        /* Subir el archivo seleccionado a Firebase */
                        uploadFile(selectedMediaUri, filename);
                    } else {
                        Log.d(TAG, "SUBIDA CANCELADA.");
                    }
                }
        );

        /* Abre la galería de imágenes del dispositivo */
        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Bundle bundle = result.getData().getExtras();
                        selectedImageToSend = (Bitmap) bundle.get("data");
                        Log.d(TAG, "Imagen recibida");

                        ImageView selected = new ImageView(getApplicationContext());
                        selected.setImageBitmap(selectedImageToSend);
                        uploadImage(selected);

                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        Toast.makeText(getApplicationContext(), "Acción cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        launchers.put(FILE_LAUNCHER, fileLauncher);
        launchers.put(GALLERY_LAUNCHER, galleryLauncher);

        return launchers;
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addListeners() {
        Map<String, ActivityResultLauncher> launchers = configureLaunchers();
        ActivityResultLauncher fileLauncher = launchers.get(FILE_LAUNCHER);
        ActivityResultLauncher galleryLauncher = launchers.get(GALLERY_LAUNCHER);


        /* Accion enviar mensaje */
        btEnviarMensaje.setOnClickListener(view -> {
            Log.i(TAG, "Enviando mensaje...");

            String contenidoMensaje = txMensajeAEnviar.getText().toString();

            if (contenidoMensaje.trim().isEmpty()) {
                Toast.makeText(ChatActivity.this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            MensajeDto newMsgDto = new MensajeDto();
            newMsgDto.contenido = contenidoMensaje;
            newMsgDto.createdAt = DateUtils.getNowWithPredefinedFormat();
            newMsgDto.status = MensajeStatus.ENVIADO;

            assert userInSession != null;
            Log.d(TAG, originChatDataDto.receiverUid + " " + userInSession.getUid());

            if (originChatDataDto != null) {
                ChatService.getInstance().sendMessage(newMsgDto, originChatDataDto, userInSession.getUid(), () -> {
                    msgAdapter.notifyDataSetChanged();
                    txMensajeAEnviar.setText("");
                    recyclerConversacionChat.smoothScrollToPosition(View.FOCUS_DOWN);
                });
            }


        });

        /* Accion seleccionar archivo para enviar */
        btSubirArchivoChat.setOnClickListener(view -> {
            Log.d(TAG, "BT-ADJUNTAR ARCHIVO: ");

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");

            assert fileLauncher != null;
            fileLauncher.launch(intent);
        });

        /* Accion volver a la lista de chats */
        btVolverListaChats.setOnClickListener(view -> startActivity(new Intent(ChatActivity.this, ListarChatsActivity.class)));

        /* Accion llamar al alumno del chat */
        btLlamarReceiver.setOnClickListener(view -> {
            if (originChatDataDto != null) {
                Intent email = new Intent(Intent.ACTION_SEND);
                //userInSession.getEmail()
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"kikocoya@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "HelpMe App - Un compañero necesita tu ayuda.");
                assert userInSession != null;
                email.putExtra(Intent.EXTRA_TEXT, "Hola, soy " + userInSession.getDisplayName() + "\n\n\nHelpMe App");

                email.setType("text/plain");

                startActivity(Intent.createChooser(email, "Enviando correo..."));
            }
        });

        /* Accion sacar foto para enviar por chat */
        btCamera.setOnClickListener(view -> {
            Log.d(TAG, "CAMERA: ");

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            assert galleryLauncher != null;
            galleryLauncher.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        paintChatMessages();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void uploadImage(ImageView imageView) {
        if (originChatDataDto == null) {
            return;
        }

        Log.i(TAG, "bytes: " + Arrays.toString(ContentTypeUtils.getImageBytes(imageView))
                + "\t" + ContentTypeUtils.isImageSizeValid(imageView));

        if (ContentTypeUtils.isImageSizeValid(imageView)) {
            Toast.makeText(this, "Tamaño subida máximo: 1MB", Toast.LENGTH_LONG).show();
            return;
        }

        ChatService.getInstance().uploadImage(imageView, originChatDataDto, () -> {
            Log.d(TAG, "Imagen subida al servidor!");
            msgAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "Recycle"})
    private void uploadFile(final Uri fileUri, final String filename) {
        if (originChatDataDto == null) {
            return;
        }

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(fileUri, "r");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error al buscar el archivo. " + e.getMessage());
            e.printStackTrace();
        }
        assert fileDescriptor != null;
        long fileSize = fileDescriptor.getLength();

        if (fileSize >= ContentTypeUtils.MAX_FILE_SIZE_TO_UPLOAD) {
            Log.e(TAG, "Error de subida. Solo se admiten archivos inferiores a 1MB.");
            Toast.makeText(this, "Tamaño máximo de subida: 1MB", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(TAG, "\nTamaño del archivo a subir: " + fileSize + " bytes");

        ChatService.getInstance().uploadFile(fileUri, originChatDataDto, filename, () -> {
            Log.d(TAG, "Cambio recibido !");
            msgAdapter.notifyDataSetChanged();
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

                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatMessages.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                Map<String, Object> resData = ((HashMap<String, Object>) ds.getValue());

                                MensajeDto newMessage = new MensajeDto();

                                assert resData != null;
                                String content = Objects.requireNonNull(resData.get(Mensaje.CONTENT)).toString();
                                String createdAt = Objects.requireNonNull(resData.get(Mensaje.CREATED_AT)).toString();

                                newMessage.contenido = content;
                                newMessage.createdAt = createdAt;
                                newMessage.mimeType = Objects.requireNonNull(resData.get(Mensaje.MESSAGE_TYPE)).toString();
                                newMessage.userUid = Objects.requireNonNull(resData.get(Mensaje.SENDER)).toString();

                                // Si el mensaje ha sido enviado por un alumno que ya se ha dado de baja
                                try {
                                    if (resData.get(Mensaje.DELETED) != null
                                            && resData.get(Mensaje.DELETED_AT) != null) {
                                        newMessage.deleted = String.valueOf(resData.get(Mensaje.DELETED));
                                        newMessage.deletedAt = (String) resData.get(Mensaje.DELETED_AT);
                                    }
                                } catch (ClassCastException cce) {
                                    Log.e(TAG, "Error al convertir los datos del mensaje eliminado. " + cce.getMessage());
                                    cce.printStackTrace();
                                }

                                Object status = resData.get(Mensaje.STATUS);

                                if (status != null) {
                                    MensajeStatus msgStatus = MensajeStatus.valueOf(status.toString());

                                    /* Si el mensaje no está leido, cambiar su estado a LEIDO */
                                    if (msgStatus.equals(MensajeStatus.RECIBIDO)
                                            || msgStatus.equals(MensajeStatus.ENVIADO)
                                            && resData.get(Mensaje.SENDER) == userInSession.getUid()) {

                                        Map<String, Object> statusPayload = new HashMap<>();
                                        statusPayload.put(Mensaje.STATUS, MensajeStatus.LEIDO);

                                        dbReference.child(Chat.REFERENCE)
                                                .child(originChatDataDto.chatId)
                                                .child(Mensaje.REFERENCE)
                                                .child(Objects.requireNonNull(ds.getKey()))
                                                .updateChildren(statusPayload).addOnSuccessListener(unused -> newMessage.status = MensajeStatus.LEIDO);
                                    }

                                    newMessage.status = msgStatus;
                                } else {
                                    newMessage.status = MensajeStatus.LEIDO;
                                }

                                newMessage.status = resData.get(Mensaje.STATUS) != null
                                        ? MensajeStatus.valueOf(Objects.requireNonNull(resData.get(Mensaje.STATUS)).toString())
                                        : MensajeStatus.LEIDO;


                                if (resData.get(Mensaje.FILE_PRETTY_TYPE) != null) {
                                    newMessage.prettyType = Objects.requireNonNull(resData.get(Mensaje.FILE_PRETTY_TYPE)).toString();
                                }

                                if (resData.get(Mensaje.FILE_SIZE) != null) {
                                    newMessage.prettySize = Objects.requireNonNull(resData.get(Mensaje.FILE_SIZE)).toString();
                                }

                                if (resData.get(Mensaje.FILE_NAME) != null) {
                                    newMessage.filename = Objects.requireNonNull(resData.get(Mensaje.FILE_NAME)).toString();
                                }

                                chatMessages.add(newMessage);
                                msgAdapter.notifyDataSetChanged();
                            }
                            msgAdapter.sortMessages();
                            msgAdapter.notifyDataSetChanged();
                            recyclerConversacionChat.smoothScrollToPosition(View.FOCUS_DOWN);
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