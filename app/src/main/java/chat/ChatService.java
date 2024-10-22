package chat;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Chat;
import com.example.helpme.model.Mensaje;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import dto.ChatSummaryDto;
import dto.MensajeDto;
import util.ContentTypeUtils;
import util.DateUtils;
import util.StringUtils;

public class ChatService {

    public static final String DEFAULT_MIME_IMG = "image/jpeg";
    public static final String DB_URL = "https://helpme-app-435b7-default-rtdb.europe-west1.firebasedatabase.app";
    public static final String TAG = "CHAT_SERVICE";
    public static final String CLOUD_STORAGE_URL = "gs://helpme-app-435b7.appspot.com/";
    public static final String BASE_PATH_CLOUD_STORAGE = "chats";

    FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);
    private static ChatService instance;

    private final FirebaseStorage cloudStorage = FirebaseStorage.getInstance(CLOUD_STORAGE_URL);
    private final StorageReference storageRef = cloudStorage.getReference();
    private final StorageReference chatStorageRef = storageRef.child(BASE_PATH_CLOUD_STORAGE);

    private final FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseStorage getCloudStorage() {
        return cloudStorage;
    }

    public StorageReference getDefaultStorage() {
        return storageRef;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(final MensajeDto msg, final ChatSummaryDto summary, final String userInSessionUid, MensajeCallback callback) {
        String msg_id = UUID.randomUUID().toString();

        Map<String, Object> payload = new HashMap<>();

        if (userInSessionUid == null
                || userInSessionUid.equals(summary.receiverUid)) {
            assert userInSession != null;
            Log.e(TAG, "El mensaje no se puede enviar. " + userInSession.getUid() + " " + summary.receiverUid);
            return;
        }

        payload.put(Mensaje.SENDER, userInSessionUid);
        payload.put(Mensaje.RECEIVER, summary.receiverUid);
        payload.put(Mensaje.CONTENT, msg.contenido);
        /* El estado del mensaje pasa a : ENVIADO. */
        payload.put(Mensaje.STATUS, msg.status);
        payload.put(Mensaje.MESSAGE_TYPE, Mensaje.DEFAULT_TYPE);
        payload.put(Mensaje.CREATED_AT, msg.createdAt);

        db.getReference().child(Chat.REFERENCE)
                .child(summary.chatId)
                .child(Mensaje.REFERENCE)
                .child(msg_id)
                .updateChildren(payload)
                .addOnSuccessListener(unused -> callback.callback())
                .addOnFailureListener(e -> Log.i(TAG, "ERROR AL ENVIAR EL MENSAJE"));
    }

    /**
     * Subir una imagen al servicio de firebase cloud.
     *
     * @param summary
     * @param callback
     */
    public void uploadImage(final ImageView imageView, final ChatSummaryDto summary, final MensajeCallback callback) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String imgUid = UUID.randomUUID().toString();
        String imageName = imgUid + ".jpg";

        UploadTask uploadTask = chatStorageRef.child(summary.chatId).child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(exception -> Log.e(TAG, "Error al subir la imagen a Cloud Storage")).addOnSuccessListener(taskSnapshot -> {

            if (taskSnapshot.getTask().isSuccessful()) {
                Log.d(TAG, "Imagen subida: " + taskSnapshot.getMetadata());
                Map<String, Object> payload = new HashMap<>();

                assert userInSession != null;
                payload.put(Mensaje.SENDER, userInSession.getUid());
                payload.put(Mensaje.RECEIVER, summary.receiverUid);
                payload.put(Mensaje.CONTENT, chatStorageRef.child(imageName).getPath());
                payload.put(Mensaje.MESSAGE_TYPE, "image/jpeg");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    payload.put(Mensaje.CREATED_AT, DateUtils.getNowWithPredefinedFormat());
                }

                db.getReference().child(Chat.REFERENCE).child(summary.chatId).child(Mensaje.REFERENCE).child(imgUid).updateChildren(payload).addOnCompleteListener(task -> callback.callback()).addOnFailureListener(e -> Log.e(TAG, "ERROR al subir la imagen al servidor. " + e.getMessage()));
            }
        });
    }

    /**
     * Subida de un archivo seleccionado en el chat al storage de Firebase.
     */
    public void uploadFile(final Uri fileUri, final ChatSummaryDto summary, final String filename, final MensajeCallback callback) {
        String docUid = UUID.randomUUID().toString();

        StorageReference uploadRef = chatStorageRef.child(summary.chatId).child(docUid);
        UploadTask uploadTask = uploadRef.putFile(fileUri);

        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.e(TAG, "Error al subir el archivo a Firebase. " + exception.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Map<String, Object> payload = new HashMap<>();

            String contentType = Objects.requireNonNull(taskSnapshot.getMetadata()).getContentType();

            assert userInSession != null;
            payload.put(Mensaje.SENDER, userInSession.getUid());
            payload.put(Mensaje.RECEIVER, summary.receiverUid);
            payload.put(Mensaje.CONTENT, "/chats/" + summary.chatId + "/" + docUid);

            payload.put(Mensaje.MESSAGE_TYPE, taskSnapshot.getMetadata().getContentType());
            payload.put(Mensaje.FILE_SIZE, StringUtils.prettyBytesSize(taskSnapshot.getTotalByteCount()));
            payload.put(Mensaje.FILE_NAME, filename);

            /* Ejemplo: application/msword --> word */
            if (ContentTypeUtils.getAvailableTypes().containsKey(contentType)) {
                payload.put(Mensaje.FILE_PRETTY_TYPE, ContentTypeUtils.getAvailableTypes().get(contentType));
            }

            /* Metadatos para los PDF */

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                payload.put(Mensaje.CREATED_AT, DateUtils.getNowWithPredefinedFormat());
            }

            Log.d(TAG, "payload: " + payload);

            db.getReference()
                    .child(Chat.REFERENCE)
                    .child(summary.chatId)
                    .child(Mensaje.REFERENCE)
                    .child(docUid).updateChildren(payload).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.callback();
                        }
                    }).addOnFailureListener(e -> Log.e(TAG, "Error al subir el archivo"));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<MensajeDto> getSortedMessages(final List<MensajeDto> messages) {
        if (messages == null) {
            return null;
        }

        Comparator<MensajeDto> msgComparator = Comparator.comparing(msg -> DateUtils.convertStringToLocalDateTime(msg.createdAt, 0));
        messages.sort(msgComparator);

        return messages;
    }

    /**
     * Cambia el estado en el chat del alumno en sesión.
     *
     * @param newStatus Nuevo estado de conexión.
     * @see #changeCurrentUserStatus(String, AlumnoStatusCallback)
     */
    public void changeCurrentUserStatus(final String newStatus, AlumnoStatusCallback callback) {
        assert userInSession != null;
        changeAlumnoStatus(userInSession.getUid(), newStatus, callback);
    }

    /**
     * Cambia el estado en el chat del alumno indicado.
     * Si el alumno se logea, su estado cambiará a ONLINE.
     *
     * @param alumnoUid Uid del alumno.
     * @param newStatus Nuevo estado de conexión del alumno.
     */
    public void changeAlumnoStatus(final String alumnoUid, final String newStatus, AlumnoStatusCallback callback) {
        if (alumnoUid == null || newStatus == null) {
            return;
        }

        Map<String, Object> alumnoPayload = new HashMap<>();
        alumnoPayload.put(Alumno.STATUS, newStatus);

        db.getReference()
                .child(Alumno.REFERENCE)
                .child(alumnoUid)
                .updateChildren(alumnoPayload).addOnSuccessListener(unused -> {
                    Log.d(TAG, "Alumno en línea !");
                    callback.callback();
                })
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }

    public boolean hasCurrentChatWithSpecificAlumno(final String alumnoUid, final AlumnoChatCallback callback) {
        db.getReference()
                .child(Chat.REFERENCE).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Set<String> uids = new HashSet<>();
                            for (DataSnapshot ds : snapshot.getChildren()) {

                                if (((HashMap<String, Object>) Objects.requireNonNull(ds.getValue())).get(Chat.ALUMNO_A) != null &&
                                        ((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_B) != null &&
                                        ((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_A) == Objects.requireNonNull(userInSession).getUid() ||
                                        ((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_B) == Objects.requireNonNull(userInSession).getUid()) {

                                    String uidAlumnoA = Objects.requireNonNull(((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_A)).toString();
                                    String uidAlumnoB = Objects.requireNonNull(((HashMap<String, Object>) ds.getValue()).get(Chat.ALUMNO_B)).toString();

                                    uids.add(uidAlumnoA);
                                    uids.add(uidAlumnoB);
                                }
                            }
                            Set<String> filteredUids = uids.stream().filter(uid -> !uid.equals(userInSession.getUid())).collect(Collectors.toSet());
                            callback.callback(filteredUids);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return false;

    }

    public void markAlumnoAsDeleted(final String chatUid, final String alumnoUid) {
        db.getReference()
                .child(Chat.REFERENCE)
                .child(chatUid)
                .child(Mensaje.REFERENCE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot msg : snapshot.getChildren()) {
                            Map<String, Object> content = (Map<String, Object>) msg.getValue();

                            if (content.get(Mensaje.SENDER).equals(alumnoUid)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    markMessageAsDeleted(chatUid, alumnoUid, msg.getKey());
                                }
                                Log.d(TAG, "MSG CONTENT: " + content.get(Mensaje.CONTENT));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void markMessageAsDeleted(final String chatUid, final String alumnoUid, final String msgUid) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("deleted", true);
        payload.put("deleted_at", DateUtils.getNowWithPredefinedFormat());

        db.getReference()
                .child(Chat.REFERENCE)
                .child(chatUid)
                .child(Mensaje.REFERENCE)
                .child(msgUid)
                .updateChildren(payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "MESSAGE DELETED SUCCESSFULLY");
                    }
                });
    }

    public void markChatAsDeleteForDeletedUser(final String userUid) {
        db.getReference()
                .child(Chat.REFERENCE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String chatUid = (String) ds.getKey();
                            markAlumnoAsDeleted(chatUid, userUid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void removeChat(final String chatId, final ChatCallback callback) {
        db.getReference()
                .child(Chat.REFERENCE)
                .child(chatId)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure();
                    }
                });
    }

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public interface MensajeCallback {
        void callback();
    }

    public interface AlumnoStatusCallback {
        void callback();
    }

    public interface AlumnoChatCallback {
        void callback(Set<String> uidsAlumnosChat);
    }

    public interface ChatCallback {
        void onSuccess();

        void onFailure();
    }
}
