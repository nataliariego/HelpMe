package adapter;

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
import com.example.helpme.model.Mensaje;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dto.ChatSummaryDto;
import dto.MensajeDto;
import util.DateUtils;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static final String TAG = "CHAT_ADAPTER";

    private List<ChatSummaryDto> chats = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(ChatSummaryDto item);
    }

    public final OnItemClickListener listener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ChatAdapter(List<ChatSummaryDto> chats, OnItemClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_listado_chats, parent, false);

        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatSummaryDto summary = chats.get(position);
        holder.bindChat(summary);
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPerfilAlumnoReceiver;
        private TextView txNombreAlumnoReceiver;
        private TextView txUltimoMensajeChat;
        private TextView txHoraUltimoMensajeChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPerfilAlumnoReceiver = (ImageView) itemView.findViewById(R.id.img_perfil_alumno_receiver_chat);
            txNombreAlumnoReceiver = (TextView) itemView.findViewById(R.id.text_nombre_chat_receiver);
            txUltimoMensajeChat = (TextView) itemView.findViewById(R.id.text_ultimo_mensaje_chat);
            txHoraUltimoMensajeChat = (TextView) itemView.findViewById(R.id.text_chat_hora_envio_ultimo_mensaje);


        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private MensajeDto getLastChatMessages(final ChatSummaryDto summary) {
            if (summary.messages == null || summary.messages.size() == 0) {
                return null;
            }

            MensajeDto last = new MensajeDto();
            long mostRecentDate = Long.MAX_VALUE;

            for (Map.Entry<String, Object> msg : summary.messages.entrySet()) {
                Map<String, Object> rawMessageData = (HashMap<String, Object>) msg.getValue();

                MensajeDto msgData = new MensajeDto();
                msgData.createdAt = rawMessageData.get(Mensaje.CREATED_AT).toString();
                msgData.contenido = rawMessageData.get(Mensaje.CONTENT).toString();
                msgData.userUid = rawMessageData.get(Mensaje.RECEIVER).toString();

                LocalDateTime msgDataCreatedAt = DateUtils.convertStringToLocalDateTime(msgData.createdAt, 0);
                LocalDateTime now = LocalDateTime.now();
                if (ChronoUnit.SECONDS.between(msgDataCreatedAt, now) < mostRecentDate) {
                    last = msgData;
                    mostRecentDate = ChronoUnit.SECONDS.between(msgDataCreatedAt, now);
                }
            }

            return last;
        }

        public void bindChat(final ChatSummaryDto summary) {

            if (summary.messages != null && summary.messages.size() > 0) {
                Log.d(TAG, "ULTIMO MENSAJES: " + summary.messages.toString());
                //txHoraUltimoMensajeChat.setText(summary.messages.get(0).toString());
                //MensajeDto ultimoMensaje = (MensajeDto) summary.messages.get(0);
                Map<String, Object> chatMessages = summary.messages;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    MensajeDto lastMessage = getLastChatMessages(summary);

                    Log.d(TAG, "ULTIMO MENSAJE: " + lastMessage.toString());

                    /* Si el último mensaje tiene más de 30 caracteres, se corta y se le añaden "..." */
                    String lastMessageSummary = lastMessage.contenido.length() > 30 ? lastMessage.contenido.substring(0, 30).concat("...") : lastMessage.contenido;
                    txUltimoMensajeChat.setText(lastMessageSummary);
                    txHoraUltimoMensajeChat.setText(DateUtils.getSimplifiedDate(lastMessage.createdAt));
                }


            } else {
                txUltimoMensajeChat.setText("Sin mensajes");
            }
            txNombreAlumnoReceiver.setText(summary.receiverName);

            /* URL de la imagen de perfil, en su defecto un placeholder con las iniciales del nombre. */
            String profileImgUrl = summary.receiverProfileImage == null || summary.receiverProfileImage.isEmpty()
                    ? "https://ui-avatars.com/api/?name=" + summary.receiverName
                    : summary.receiverProfileImage;

            Picasso.get().load(profileImgUrl).into(imgPerfilAlumnoReceiver);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(summary);
                }
            });
        }
    }
}
