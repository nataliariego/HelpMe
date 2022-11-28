package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dto.ChatSummaryDto;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static final String TAG = "CAHT_ADAPTER";

    private List<ChatSummaryDto> chats = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(ChatSummaryDto item);
    }

    public final OnItemClickListener listener;

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

        public void bindChat(final ChatSummaryDto summary) {
            txUltimoMensajeChat.setText(summary.lastMessage);
            txHoraUltimoMensajeChat.setText(summary.lastMessageTimestamp);
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
