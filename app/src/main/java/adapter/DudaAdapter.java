package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.model.Duda;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DudaAdapter extends RecyclerView.Adapter<DudaAdapter.DudaViewHolder> {

    private List<Duda> dudas = new ArrayList<>();
    private final AdapterView.OnItemClickListener listener;

    public DudaAdapter(List<Duda> dudas, AdapterView.OnItemClickListener listener) {
        this.dudas = dudas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.duda_resumen_card, parent, false);
        return new DudaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DudaViewHolder holder, int position) {
       Duda duda = dudas.get(position);

        holder.titulo.setText(duda.getTitulo());
        holder.abrevMateria.setText(duda.getMateria().getAbreviatura());
        holder.fechaPublicacion.setText(duda.getFecha().toString());
        holder.nombreAlumno.setText(duda.getAlumno().getNombre());
    }

    @Override
    public int getItemCount() {
        return dudas.size();
    }

    protected class DudaViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo;
        private TextView nombreAlumno;
        private TextView fechaPublicacion;
        private TextView abrevMateria;

        public DudaViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txTituloDuda);
            nombreAlumno = itemView.findViewById(R.id.txNombreAlumnoDudaResumen);
            fechaPublicacion = itemView.findViewById(R.id.txResumenFechaPublicacionDuda);
            abrevMateria = itemView.findViewById(R.id.txResumenDudaAbrevMateria);
        }

        public void bindDuda(final Duda duda, final AdapterView.OnItemClickListener listener) {
            titulo.setText(duda.getTitulo());
            nombreAlumno.setText(duda.getAlumno().getNombre());
            fechaPublicacion.setText(duda.getFecha());
            abrevMateria.setText(duda.getMateria().getAbreviatura());

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemClick(duda);
//                }
//            });
        }
    }
}
