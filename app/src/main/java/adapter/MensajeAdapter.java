package adapter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static chat.ChatService.DEFAULT_MIME_IMG;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.model.Mensaje;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageMetadata;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chat.ChatService;
import chat.MensajeStatus;
import dto.MensajeDto;
import util.DateUtils;
import util.StringUtils;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    public static final String TAG = "MENSAJE_ADAPTER";

    public static final int SENDER_POSITION = 0;
    public static final int RECEIVER_POSITION = 1;
    public static final int DOCUMENT_SENDER_POSITION = 2;
    public static final int DOCUMENT_RECEIVER_POSITION = 3;

    private List<MensajeDto> mensajes = new ArrayList<>();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    public MensajeAdapter(List<MensajeDto> mensajes) {
    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "MENSAJES ORDENADOS: " + ChatService.getInstance().getSortedMessages(mensajes));
            this.mensajes = ChatService.getInstance().getSortedMessages(mensajes);
        }else{
        }*/
        this.mensajes = mensajes;
    }

    public void sortMessages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.mensajes = ChatService.getInstance().getSortedMessages(this.mensajes);
        }
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int selectedLayout = getSelectedLayout(viewType);

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(selectedLayout, parent, false);

        return new MensajeViewHolder(itemView);
    }

    private int getSelectedLayout(int viewType) {
        int selectedLayout = -1;

        /* izda */
        if (viewType == DOCUMENT_SENDER_POSITION) {
            selectedLayout = R.layout.document_attached_message_chat_sender;

            /* dcha */
        } else if (viewType == DOCUMENT_RECEIVER_POSITION) {
            selectedLayout = R.layout.document_attached_message_chat_receiver;

            /* dcha */
        } else if (viewType == RECEIVER_POSITION) {
            selectedLayout = R.layout.mensaje_chat_sender;

            /* izda */
        } else if (viewType == SENDER_POSITION) {
            selectedLayout = R.layout.mensaje_chat_receiver;
        }
        return selectedLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        MensajeDto msg = mensajes.get(position);
        holder.bindMensaje(msg);
    }

    @Override
    public int getItemCount() {
        return mensajes == null ? 0 : mensajes.size();
    }

    @Override
    public int getItemViewType(int position) {
        /* Si el mensaje es un documento */
        if (mensajes.get(position).mimeType.contains("application") ||
                mensajes.get(position).mimeType.equals("text/plain")
        ) {
            /* En función de quién lo envíe, se muestra a un lado u otro de la pantalla */
            if (mensajes.get(position).userUid.equals(userInSession.getUid())) {
                return DOCUMENT_RECEIVER_POSITION;
            } else {
                return DOCUMENT_SENDER_POSITION;
            }

        } else {
            /* En función de quién lo envíe, se muestra a un lado u otro de la pantalla */
            if (mensajes.get(position).userUid.equals(userInSession.getUid())) {
                return RECEIVER_POSITION;
            } else {
                return SENDER_POSITION ;
            }
        }
    }

    class MensajeViewHolder extends RecyclerView.ViewHolder {

        private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

        private TextView txContenidoMensaje;
        private TextView txFechaEnvioMensaje;
        private ImageView imgContenidoImagen;

        /* Layout para los documentos */
        private TextView txMimeDocumento;
        private TextView txNombreDocumento;
        private TextView txMetadatosDocumento;
        private ImageButton btDescargarDocumento;
        private TextView txHoraEnvioDocumento;
        //private ImageView iconEstadoMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            txContenidoMensaje = itemView.findViewById(R.id.text_contenido_mensaje_conversacion);
            txFechaEnvioMensaje = itemView.findViewById(R.id.text_hora_envio_documento_conversacion);
            imgContenidoImagen = itemView.findViewById(R.id.img_imagen_mensaje);

            /* Metadatos del documento (mensaje) */
            txMimeDocumento = (TextView) itemView.findViewById(R.id.text_mime_message);
            txNombreDocumento = (TextView) itemView.findViewById(R.id.text_message_document_name);
            txMetadatosDocumento = (TextView) itemView.findViewById(R.id.text_message_document_metadata);
            btDescargarDocumento = (ImageButton) itemView.findViewById(R.id.img_download_document_message);
            txHoraEnvioDocumento = (TextView) itemView.findViewById(R.id.text_hora_envio_documento_conversacion);
            /* Ticks estado mensaje  */
            //iconEstadoMensaje = (ImageView) itemView.findViewById(R.id.img_estado_envio_mensaje);

        }


        /**
         * Carga la URI de la imagen almacenada en el campo contenido del mensaje.
         *
         * @param path
         * @param callback
         * @return
         */
        private void loadImage(final String path, final ImageMessageCallback callback) {
            //10MB
            long MAX_BYTES = (long) Math.pow(1024, 10);

            Log.d(TAG, ChatService.getInstance().getCloudStorage().getReference(path.substring(1)).toString());

            ChatService.getInstance().getCloudStorage().getReference(path.substring(1))
                    .getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d(TAG, "Imagen cargada!");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgContenidoImagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imgContenidoImagen.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600, 900, false));

                            callback.callback();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "Error al cargar la imagen del mensaje");
                        }
                    });
        }


        /**
         * Carga el documento almacenado en Firebase Storage en la ruta <code>path</code>
         *
         * @param path     Ruta del documento.
         * @param callback Callback de respuesta si se realiza la carga satisfactoriamente.
         */
        private void loadDocument(final String path, final DocumentMessageCallback callback) {
            ChatService.getInstance()
                    .getCloudStorage()
                    .getReference(path.substring(1))
                    .getMetadata()
                    .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            Log.d(TAG, "Documento cargado correctamente! ");

                            Map<String, Object> documentPayload = new HashMap<>();

                            documentPayload.put(Mensaje.FILE_SIZE, StringUtils.prettyBytesSize(storageMetadata.getSizeBytes()));
                            documentPayload.put(Mensaje.FILE_NAME, StringUtils.shortWordMaxCharacters(storageMetadata.getName(), 20));
                            documentPayload.put(Mensaje.MESSAGE_TYPE, String.valueOf(storageMetadata.getContentType()));

                            callback.callback(documentPayload);
                        }
                    });
        }

        /**
         * Establece el icono del estado del mensaje en función del valor
         * de éste.
         *
         * @param --status Estado del mensaje
         * @see {@link MensajeStatus}
         */
//        private void setStatusIcon(final MensajeStatus status) {
//            Log.d(TAG, "ADAPT. STATUS = " + status.toString());
//            if (status.equals(MensajeStatus.ENVIADO)) {
//                iconEstadoMensaje.setImageResource(R.drawable.ic_round_done_24);
//                iconEstadoMensaje.setVisibility(View.VISIBLE);
//
//            } else if (status.equals(MensajeStatus.RECIBIDO)) {
//                iconEstadoMensaje.setImageResource(R.drawable.ic_icon_recibido_24);
//                iconEstadoMensaje.setVisibility(View.VISIBLE);
//
//            } else if (status.equals(MensajeStatus.LEIDO)) {
//                iconEstadoMensaje.setImageResource(R.drawable.ic_round_done_all_24);
//                iconEstadoMensaje.setVisibility(View.VISIBLE);
//
//            } else {
//                iconEstadoMensaje.setVisibility(View.GONE);
//            }
//        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindMensaje(final MensajeDto msg) {

            LocalDateTime timestamp = DateUtils.convertStringToLocalDateTime(msg.createdAt, 0);
            String hour = String.format("%02d", timestamp.getHour());
            String minutes = String.format("%02d", timestamp.getMinute());
            String msgDateFormatted = hour.concat(":").concat(minutes);

//            if (msg.status != null && iconEstadoMensaje != null) {
//                setStatusIcon(msg.status);
//            }

            /* Si el contenido del mensaje a mostrar es una imagen */
            if (msg.mimeType.equals(DEFAULT_MIME_IMG)) {
                txContenidoMensaje.setVisibility(View.GONE);

                /* Cargar la imagen */
                loadImage(msg.contenido, new ImageMessageCallback() {
                    @Override
                    public void callback() {
                        imgContenidoImagen.setVisibility(View.VISIBLE);
                        txFechaEnvioMensaje.setText(msgDateFormatted);
                    }
                });
            } else if (msg.mimeType.contains("application") ||
                    msg.mimeType.equals("text/plain")) {

                loadDocument(msg.contenido, new DocumentMessageCallback() {
                    @Override
                    public void callback(Map<String, Object> payload) {
                        Log.d(TAG, String.valueOf(payload));

                        String docMime = msg.mimeType;
                        String docType = msg.prettyType;
                        String docSize = msg.prettySize;
                        String docFilename = msg.filename;

                        txMimeDocumento.setText(docType != null ? docType : docMime);
                        txNombreDocumento.setText(StringUtils.shortWordMaxCharacters(docFilename, 10));

                        if (docSize != null) {
                            txMetadatosDocumento.setText(docSize);
                        }

                        if (txHoraEnvioDocumento != null) {
                            txHoraEnvioDocumento.setText(msgDateFormatted);
                        }
                        if (btDescargarDocumento != null) {
                            btDescargarDocumento.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ChatService.getInstance().getDefaultStorage().child(msg.contenido).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            /* Descargar el documento */
                                            DownloadManager downloadManager = (DownloadManager) itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                            DownloadManager.Request request = new DownloadManager.Request(uri);

                                            /* Mostrar notificacion de descarga */
                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                            request.setDestinationInExternalFilesDir(itemView.getContext(), DIRECTORY_DOWNLOADS, payload.get("name").toString() + "." + payload.get("type").toString());

                                            downloadManager.enqueue(request);

                                            Toast.makeText(itemView.getContext(), "Documento descargado", Toast.LENGTH_SHORT);
                                        }
                                    });
                                    Log.d(TAG, "Descargando documento...");
                                }
                            });
                        }
                    }
                });

            } else {

                txContenidoMensaje.setText(msg.contenido);

                txFechaEnvioMensaje.setText(msgDateFormatted);

                if (imgContenidoImagen != null) {
                    imgContenidoImagen.setVisibility(View.GONE);
                }
                txContenidoMensaje.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     * Respuesta de la carga de una imagen.
     */
    public interface ImageMessageCallback {
        void callback();
    }

    /*
     * Respuesta de la carga de un documento.
     */
    public interface DocumentMessageCallback {
        void callback(Map<String, Object> payload);
    }
}
