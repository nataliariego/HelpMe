package adapter;

import static chat.ChatService.DEFAULT_MIME_IMG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import chat.ChatService;
import dto.MensajeDto;
import util.DateUtils;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    public static final String TAG = "MENSAJE_ADAPTER";

    public static final int SENDER_POSITION = 0;
    public static final int RECEIVER_POSITION = 1;

    private List<MensajeDto> mensajes = new ArrayList<>();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    public MensajeAdapter(List<MensajeDto> mensajes) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.d(TAG, "MENSAJES ORDENADOS: " + ChatService.getInstance().getSortedMessages(mensajes));
//            this.mensajes = ChatService.getInstance().getSortedMessages(mensajes);
//        }else{
//        }
            this.mensajes = mensajes;
    }

    public void sortMessages(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.mensajes = ChatService.getInstance().getSortedMessages(this.mensajes);
        }
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(viewType == RECEIVER_POSITION
                                ? R.layout.mensaje_chat_receiver
                                : R.layout.mensaje_chat_sender
                        , parent, false);

        return new MensajeViewHolder(itemView);
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
        return mensajes.get(position).userUid.equals(userInSession.getUid()) ? RECEIVER_POSITION : SENDER_POSITION;
    }

    class MensajeViewHolder extends RecyclerView.ViewHolder {

        private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

        private TextView txContenidoMensaje;
        private TextView txFechaEnvioMensaje;
        private ImageView imgContenidoImagen;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            txContenidoMensaje = itemView.findViewById(R.id.text_contenido_mensaje_conversacion);
            txFechaEnvioMensaje = itemView.findViewById(R.id.text_hora_envio_mensaje_conversacion);
            imgContenidoImagen = itemView.findViewById(R.id.img_imagen_mensaje);
        }


        /**
         * Carga la URI de la imagen almacenada en el campo contenido del mensaje.
         *
         * @param path
         * @param callback
         * @return
         */
        private void loadImage(final String path, final ImageMessageCallback callback) {
            //13MB
            long MAX_BYTES = (long) Math.pow(1024, 13);

            Log.d(TAG, ChatService.getInstance().getCloudStorage().getReference(path.substring(1)).toString());

            ChatService.getInstance().getCloudStorage().getReference(path.substring(1))
                    .getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d(TAG, "Imagen cargada!");


                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            //imgContenidoImagen.setImageBitmap(bitmap);

                            imgContenidoImagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imgContenidoImagen.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600, 900, false));
                            //Picasso.get().load(getImageUri(itemView.getContext(), bitmap)).into(imgContenidoImagen);
                            //callback.callback();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d(TAG, "Error al cargar la imagen del mensaje");
                        }
                    });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindMensaje(final MensajeDto msg) {
            txContenidoMensaje.setText(msg.contenido);
//            txFechaEnvioMensaje.setText(DateUtils.prettyDate(msg.fechaEnvio));
            LocalDateTime timestamp = DateUtils.convertStringToLocalDateTime(msg.createdAt);
            String hour = String.valueOf(timestamp.getHour());
            String minutes = String.valueOf(timestamp.getMinute());

            String timeAsString = hour.concat(":").concat(minutes);

            /* Si el contenido del mensaje a mostrar es una imagen */
            if (msg.mimeType.equals(DEFAULT_MIME_IMG)) {
                txContenidoMensaje.setVisibility(View.GONE);

                /* Cargar la imagen */
                loadImage(msg.contenido, new ImageMessageCallback() {
                    @Override
                    public void callback() {
//                        Log.d(TAG, "IMAGEN TEMP: " + tempImage.getAbsolutePath());
//                        final Transformation transformation = new RoundedCornersTransformation(5, 0);
//                        Picasso.get().load(tempImage.getAbsolutePath()).into(imgContenidoImagen);
                        imgContenidoImagen.setVisibility(View.VISIBLE);
                    }
                });


            } else {
                if (imgContenidoImagen != null) {
                    imgContenidoImagen.setVisibility(View.GONE);
                }
                txContenidoMensaje.setVisibility(View.VISIBLE);
            }

//            if(ChronoUnit.SECONDS.between(timestamp, LocalDateTime.now()) < 60){
//                timeAsString = "justo ahora";
//            }

            txFechaEnvioMensaje.setText(timeAsString);
        }
    }

    public interface ImageMessageCallback {
        void callback();
    }
}
