package adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dto.AlumnoDto;
import dto.AsignaturaDto;
import dto.RespuestaDto;

public class RespuestaAdapter extends RecyclerView.Adapter<RespuestaAdapter.RespuestaViewHolder> {

    private List<RespuestaDto> respuestas = new ArrayList<>();

    public RespuestaAdapter(List<RespuestaDto> respuestas) {
        this.respuestas = respuestas;
    }

    @NonNull
    @Override
    public RespuestaAdapter.RespuestaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_respuesta, parent, false);
        return new RespuestaAdapter.RespuestaViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RespuestaAdapter.RespuestaViewHolder holder, int position) {
        RespuestaDto res = respuestas.get(position);

        //holder.nombreAlumno.setText(alumno.nombre);

        holder.bindRespuesta(res);
    }

    @Override
    public int getItemCount() {
        return respuestas.size();
    }

    protected class RespuestaViewHolder extends RecyclerView.ViewHolder {

        private TextView nombreAlumno;
        private TextView fecha;
        private ImageView imagen_alumno;
        private TextView respuesta;

        public RespuestaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreAlumno = itemView.findViewById(R.id.txNombreAlRespuesta);
            fecha = itemView.findViewById(R.id.txFechaRespuesta);
            imagen_alumno = itemView.findViewById(R.id.img_perfil_alumno_respuesta);
            respuesta = itemView.findViewById(R.id.txRespuesta);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindRespuesta(final RespuestaDto res) {
            nombreAlumno.setText(res.nombreAlumnoResponde);
            fecha.setText(res.fecha);
            respuesta.setText(res.respuesta);
            Picasso.get().load(res.url_foto_responde).into(imagen_alumno);

        }
    }
}
