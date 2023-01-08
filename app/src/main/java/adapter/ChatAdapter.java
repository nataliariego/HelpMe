package adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.model.Mensaje;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import chat.ChatService;
import dto.ChatSummaryDto;
import dto.MensajeDto;
import util.DateUtils;

public class ChatAdapter extends
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static final String TAG = "CHAT_ADAPTER";

    private final List<ChatSummaryDto> chats;
    private ChatViewHolder chatViewHolder;


    public void resetChatStyles() {
        chatViewHolder.changeChatStyles(false);
    }

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

        chatViewHolder = new ChatViewHolder(itemView);
        return chatViewHolder;
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

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layoutLineaChat;
        private final ImageView imgPerfilAlumnoReceiver;
        private final TextView txNombreAlumnoReceiver;
        private final TextView txUltimoMensajeChat;
        private final TextView txHoraUltimoMensajeChat;

        private ChatSummaryDto selectedChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutLineaChat = (LinearLayout) itemView.findViewById(R.id.layout_linea_listado_chats);
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
                msgData.createdAt = Objects.requireNonNull(rawMessageData.get(Mensaje.CREATED_AT)).toString();
                msgData.contenido = rawMessageData.get(Mensaje.CONTENT).toString();
                msgData.userUid = Objects.requireNonNull(rawMessageData.get(Mensaje.RECEIVER)).toString();

                LocalDateTime msgDataCreatedAt = DateUtils.convertStringToLocalDateTime(msgData.createdAt, 0);
                LocalDateTime now = LocalDateTime.now();
                if (ChronoUnit.SECONDS.between(msgDataCreatedAt, now) < mostRecentDate) {
                    last = msgData;
                    mostRecentDate = ChronoUnit.SECONDS.between(msgDataCreatedAt, now);
                }
            }

            return last;
        }

        @SuppressLint("SetTextI18n")
        public void bindChat(final ChatSummaryDto summary) {

            if (summary.messages != null && summary.messages.size() > 0) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    MensajeDto lastMessage = getLastChatMessages(summary);

                    assert lastMessage != null;
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

            itemView.setOnClickListener(view -> listener.onItemClick(summary));

            /* Detectar chat seleccionado */
            itemView.setOnCreateContextMenuListener((menu, view, contextMenuInfo) -> {
                selectedChat = summary;

                MenuItem deleteOpt = menu.add(Menu.NONE, 1, 1, "Borrar chat");
                MenuItem cancelOpt = menu.add(Menu.NONE, 2, 2, "Cancelar");

                changeChatStyles(true);

                deleteOpt.setOnMenuItemClickListener(menuItem -> {
                    ChatService.getInstance().removeChat(selectedChat.chatId, new ChatService.ChatCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(itemView.getContext(), "Chat eliminado correctamente", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(itemView.getContext(), "No se pudo eliminar el chat seleccionado", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                });

                cancelOpt.setOnMenuItemClickListener(menuItem -> {
                    Toast.makeText(view.getContext(), "OPERACION CANCELADA", Toast.LENGTH_SHORT).show();
                    changeChatStyles(false);
                    return true;
                });
            });
        }

        private void changeChatStyles(final boolean selected) {

            if (selected) {
                layoutLineaChat.setBackgroundResource(R.drawable.selected_chat_summary_red);
                txNombreAlumnoReceiver.setTextColor(Color.rgb(255, 255, 255));
                txUltimoMensajeChat.setTextColor(Color.rgb(255, 255, 255));
                txHoraUltimoMensajeChat.setTextColor(Color.rgb(255, 255, 255));

            } else {
                layoutLineaChat.setBackgroundResource(0);

                txNombreAlumnoReceiver.setTextColor(Color.rgb(12, 12, 50));
                txUltimoMensajeChat.setTextColor(Color.rgb(12, 12, 50));
                txHoraUltimoMensajeChat.setTextColor(Color.rgb(12, 12, 50));
            }
        }
    }
}
