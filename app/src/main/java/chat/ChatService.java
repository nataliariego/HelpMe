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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import dto.ChatSummaryDto;
import dto.MensajeDto;
import util.ContentTypeUtils;
import util.DateUtils;
import util.StringUtils;

public class ChatService {

    public static final String DEFAULT_MIME_IMG = "image/jpeg";
    public static final String PDF_MIME_TYPE = "application/pdf";

    /* Firebase FireStore */
    public static final String DB_URL = "https://helpme-app-435b7-default-rtdb.europe-west1.firebasedatabase.app";

    /* Firebase Cloud Storage */
    public static final String CLOUD_STORAGE_URL = "gs://helpme-app-435b7.appspot.com/";
    public static final String BASE_PATH_CLOUD_STORAGE = "chats";

    /* Gestionar todas las conversaciones */ FirebaseDatabase db = FirebaseDatabase.getInstance(DB_URL);

    /* Firebase Store, se obtendrá la información de los usuarios */
    private FirebaseFirestore store = FirebaseFirestore.getInstance();

    /* Cloud Storage */
    private FirebaseStorage cloudStorage = FirebaseStorage.getInstance(CLOUD_STORAGE_URL);
    private StorageReference storageRef = cloudStorage.getReference();
    private StorageReference chatStorageRef = storageRef.child(BASE_PATH_CLOUD_STORAGE);

    private static ChatService instance;

    public static final String TAG = "CHAT_SERVICE";

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    public StorageReference getChatStorageRef() {
        return chatStorageRef;
    }

    public FirebaseStorage getCloudStorage() {
        return cloudStorage;
    }

    public StorageReference getDefaultStorage() {
        return storageRef;
    }

    /**
     * Envia un mensaje.
     *
     * @param msg
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(final MensajeDto msg, final ChatSummaryDto summary, final String userInSessionUid, MensajeCallback callback) {
        String msg_id = UUID.randomUUID().toString();

        Map<String, Object> payload = new HashMap<>();

        if (userInSessionUid == null
                || userInSessionUid.equals(summary.receiverUid)) {
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.callback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "ERROR AL ENVIAR EL MENSAJE");
                    }
                });
    }

    public void uploadImage(ImageView imageView, ChatSummaryDto summary, MensajeCallback callback) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String imgUid = UUID.randomUUID().toString();
        String imageName = imgUid + ".jpg";

        UploadTask uploadTask = chatStorageRef.child(summary.chatId).child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error al subir la imagen a Cloud Storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...

                if (taskSnapshot.getTask().isSuccessful()) {
                    Log.d(TAG, "Imagen subida: " + taskSnapshot.getMetadata());
                    Map<String, Object> payload = new HashMap<>();

                    payload.put(Mensaje.SENDER, userInSession.getUid());
                    payload.put(Mensaje.RECEIVER, summary.receiverUid);
                    payload.put(Mensaje.CONTENT, chatStorageRef.child(imageName).getPath());
                    payload.put(Mensaje.MESSAGE_TYPE, "image/jpeg");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        payload.put(Mensaje.CREATED_AT, DateUtils.getNowWithPredefinedFormat());
                    }

                    db.getReference().child(Chat.REFERENCE).child(summary.chatId).child(Mensaje.REFERENCE).child(imgUid).updateChildren(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            callback.callback();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "ERROR al subir la imagen al servidor. " + e.getMessage());
                        }
                    });
                }

            }
        });

    }

    /**
     * Subida de un archivo seleccionado en el chat al storage de Firebase.
     *
     * @param fileUri
     * @param summary
     */
    public void uploadFile(final Uri fileUri, final ChatSummaryDto summary, final String filename, final MensajeCallback callback) {
        //String refPath = "chats/" + summary.chatId + fileUri.getLastPathSegment();
        String docUid = UUID.randomUUID().toString();
        StorageReference uploadRef = chatStorageRef.child(summary.chatId).child(docUid);
        UploadTask uploadTask = uploadRef.putFile(fileUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "Error al subir el archivo a Firebase. " + exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Map<String, Object> payload = new HashMap<>();

                String contentType = taskSnapshot.getMetadata().getContentType();

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
//                if (taskSnapshot.getMetadata().getContentType().equals("application/pdf")) {
//                    Uri tempUri = taskSnapshot.getUploadSessionUri();
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    payload.put(Mensaje.CREATED_AT, DateUtils.getNowWithPredefinedFormat());
                }

                Log.d(TAG, "payload: " + payload.toString());

                db.getReference()
                        .child(Chat.REFERENCE)
                        .child(summary.chatId)
                        .child(Mensaje.REFERENCE)
                        .child(docUid).updateChildren(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.callback();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error al subir el archivo");
                            }
                        });
            }
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
     * Obtiene los mensajes del otro alumno (Alumno B) del chat.
     *
     * @param messages Lista de todos los mensajes.
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<MensajeDto> getReceiverMessages(final List<MensajeDto> messages) {
        return messages.stream().filter(m -> m.userUid != userInSession.getUid()).collect(Collectors.toList());
    }

    /**
     * Cambia el estado en el chat del alumno en sesión.
     *
     * @param newStatus Nuevo estado de conexión.
     * @see #changeCurrentUserStatus(String, AlumnoStatusCallback)
     */
    public void changeCurrentUserStatus(final String newStatus, AlumnoStatusCallback callback) {
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
                .updateChildren(alumnoPayload).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Alumno en línea !");
                        callback.callback();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
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
}
