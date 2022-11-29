package com.example.helpme;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import chat.ChatService;
import dto.AlumnoDto;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "CHAT_ACTIVITY";

    private EditText txMensajeAEnviar;
    private ImageButton btEnviarMensaje;

    private ImageView imgPerfilUsuarioReceiver;
    private TextView txNombreUsuarioReceiver;

    private RecyclerView recyclerConversacionChat;

    private DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private AlumnoDto alumnoB = new AlumnoDto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        if (savedInstanceState == null) {
            Bundle infoAlumno = getIntent().getExtras();
            if (infoAlumno != null) {

                /*  Información del alumno con el que se va a entablar conversación en el chat. */
                AlumnoDto alumno = (AlumnoDto) infoAlumno.get(ListadoAlumnosChatActivity.ALUMNO_SELECCIONADO_CHAT);
                alumnoB.email = alumno.email;
                alumnoB.urlFoto = alumno.urlFoto != null ? alumno.urlFoto : "https://ui-avatars.com/api/?name=" + alumno.nombre;
                alumnoB.nombre = alumno.nombre;
                alumnoB.uo = alumno.uo;

                Log.i(TAG, "ALUMNO-DTO-SAVED-INSTANCE-NULL: " + alumno.toString());
            }

        } else {

            AlumnoDto alumno = (AlumnoDto) savedInstanceState.getSerializable(ListadoAlumnosChatActivity.ALUMNO_SELECCIONADO_CHAT);
            Log.i(TAG, "ALUMNO-DTO-SAVED-INSTANCE-NN: " + alumno.toString());
        }


        initFields();

        btEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Enviando mensaje...");
                String receiverUid = userInSession.getUid() != "e36oOGlIZlZTGUiYfuU6lng4poo2" ? "nxg2Y3cVaUSf5RAu8R0IaYjkUoZ2" : "e36oOGlIZlZTGUiYfuU6lng4poo2";
                ChatService.getInstance().sendMessage(txMensajeAEnviar.getText().toString(), receiverUid);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        dbReference.child("messages").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {

                            Iterable<DataSnapshot> items = task.getResult().getChildren();


                            //Log.i(TAG, task.getResult().getValue().toString());
                        } else {
                            Log.e(TAG, "Error al leer los mensajes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });

        dbReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d(TAG, "ds: " + ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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