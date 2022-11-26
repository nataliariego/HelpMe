package adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.model.Mensaje;

import java.util.List;

import dto.MensajeDto;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private List<MensajeDto> mensajes;

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MensajeViewHolder extends RecyclerView.ViewHolder{

        private TextView txContenidoMensaje;
        private TextView txFechaEnvioMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            txContenidoMensaje = itemView.findViewById(R.id.text_contenido_mensaje_conversacion);
            txFechaEnvioMensaje = itemView.findViewById(R.id.text_hora_envio_mensaje_conversacion);
        }

        public void bindMensaje(MensajeDto msg){
            msg.contenido = txContenidoMensaje.getText().toString();
            msg.fechaEnvio = txFechaEnvioMensaje.getText().toString();
        }
    }
}
