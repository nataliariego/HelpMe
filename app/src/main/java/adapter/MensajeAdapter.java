package adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import dto.MensajeDto;
import util.DateUtils;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    public static final String TAG = "MENSAJE_ADAPTER";

    public static final int SENDER_POSITION = 0;
    public static final int RECEIVER_POSITION = 1;

    private List<MensajeDto> mensajes = new ArrayList<>();
    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    public MensajeAdapter(List<MensajeDto> mensajes) {
        this.mensajes = mensajes;
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
        if (mensajes.get(position).userUid != userInSession.getUid()) {
            return SENDER_POSITION;
        } else {
            return RECEIVER_POSITION;
        }
    }

    class MensajeViewHolder extends RecyclerView.ViewHolder {

        private TextView txContenidoMensaje;
        private TextView txFechaEnvioMensaje;
        private LinearLayout layoutMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            txContenidoMensaje = itemView.findViewById(R.id.text_contenido_mensaje_conversacion);
            txFechaEnvioMensaje = itemView.findViewById(R.id.text_hora_envio_mensaje_conversacion);
            layoutMensaje = itemView.findViewById(R.id.layout_mensaje_chat);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindMensaje(final MensajeDto msg) {
            txContenidoMensaje.setText(msg.contenido);
            txFechaEnvioMensaje.setText(DateUtils.prettyDate(msg.fechaEnvio));
        }
    }
}
