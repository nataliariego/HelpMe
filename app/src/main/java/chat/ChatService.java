package chat;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.helpme.model.Chat;
import com.example.helpme.model.Mensaje;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dto.ChatSummaryDto;
import dto.MensajeDto;

public class ChatService {

    public static final String DB_URL = "https://helpme-app-435b7-default-rtdb.europe-west1.firebasedatabase.app";

    /* Gestionar todas las conversaciones */
    FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);

    /* Firebase Store, se obtendrá la información de los usuarios */
    private FirebaseFirestore store = FirebaseFirestore.getInstance();

    private static ChatService instance;

    public static final String TAG = "CHAT_SERVICE";

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * Envia un mensaje.
     *
     * @param msg
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(final MensajeDto msg, final ChatSummaryDto summary) {
        String msg_id = UUID.randomUUID().toString();

        Map<String, Object> payload = new HashMap<>();

        payload.put(Mensaje.SENDER, userInSession.getUid());
        payload.put(Mensaje.RECEIVER, summary.receiverUid);
        payload.put(Mensaje.CONTENT, msg.contenido);
        // TODO: Cambiar a tipo enviado
        payload.put(Mensaje.MESSAGE_TYPE, Mensaje.DEFAULT_TYPE);
        payload.put(Mensaje.CREATED_AT, msg.fechaEnvio);

        db.getReference()
                .child(Chat.REFERENCE)
                .child(summary.chatId)
                .child(Mensaje.REFERENCE)
                .child(msg_id)
                .updateChildren(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "MENSAJE ENVIADO");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "ERROR AL ENVIAR EL MENSAJE");
                    }
                });
    }

    public void receiveMessage() {

    }

    public void showAllChats() {
        db.getReference(Chat.REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String uid = data.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

}
