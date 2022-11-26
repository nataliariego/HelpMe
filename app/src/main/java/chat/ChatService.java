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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import util.StringUtils;

public class ChatService {

    public static final String DB_URL = "https://helpme-app-435b7-default-rtdb.europe-west1.firebasedatabase.app";

    /* Gestionar todas las conversaciones */
    FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);

    /* Firebase Store, se obtendrá la información de los usuarios */
    private FirebaseFirestore store = FirebaseFirestore.getInstance();

    private static ChatService instance;

    public static final String USER_REFERENCE = "users";
    public static final String CHAT_REFERENCE = "chats";
    public static final String MSG_REFERENCE = "messages";

    public static final String TAG = "CHAT_SERVICE";

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * Envia un mensaje.
     *
     * @param msg
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(final String msg) {
        String temp_chat_uid = "451f3380-6dbd-11ed-a1eb-0242ac120002";

        Map<String, Object> payload = new HashMap<>();
        payload.put(Mensaje.CHAT_ID, temp_chat_uid);
        payload.put(Mensaje.SENDER, "sender-uid");
        payload.put(Mensaje.RECEIVER, "receiver-uid");
        payload.put(Mensaje.CREATED_AT, LocalDateTime.now());
        payload.put(Mensaje.CONTENT, msg);

        db.getReference().child(MSG_REFERENCE)
                .child(UUID.randomUUID().toString())
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

//        db.getReference().child(CHAT_REFERENCE)
//                .child(temp_chat_uid)
//                .child(userInSession.getUid()).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.i(TAG, "MENSAJE ENVIADO");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.i(TAG, "ERROR AL ENVIAR EL MENSAJE");
//                    }
//                });
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
