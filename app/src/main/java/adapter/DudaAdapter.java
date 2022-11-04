package adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;

import java.util.ArrayList;
import java.util.List;

import assembler.AlumnoAssembler;
import dto.DudaDto;
import util.DateUtils;
import util.StringUtils;

public class DudaAdapter extends RecyclerView.Adapter<DudaAdapter.DudaViewHolder> {

    private List<DudaDto> dudas = new ArrayList<>();
//    private final AdapterView.OnItemClickListener listener;

    public DudaAdapter(List<DudaDto> dudas) {
        this.dudas = dudas;
    }

    @NonNull
    @Override
    public DudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.duda_resumen_card, parent, false);
        return new DudaViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DudaViewHolder holder, int position) {
        DudaDto duda = dudas.get(position);

        holder.titulo.setText(duda.titulo);
        //holder.fechaPublicacion.setText(DateUtils.prettyDate(duda.fecha));
        holder.fechaPublicacion.setText(duda.fecha);
        holder.nombreAlumno.setText(AlumnoAssembler.toDto(duda.alumno).nombre);

        holder.bindDuda(duda);
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
        private TextView siglasAlumno;

        public DudaViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txTituloDuda);
            nombreAlumno = itemView.findViewById(R.id.txNombreAlumnoDudaResumen);
            fechaPublicacion = itemView.findViewById(R.id.txResumenFechaPublicacionDuda);
            abrevMateria = itemView.findViewById(R.id.txResumenDudaAbrevMateria);
            siglasAlumno = itemView.findViewById(R.id.tx_siglas_alumno_avatar);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindDuda(final DudaDto duda) {
            titulo.setText(duda.titulo);
            //fechaPublicacion.setText(DateUtils.prettyDate(duda.fecha));
            fechaPublicacion.setText(duda.fecha);
            siglasAlumno.setText(StringUtils.getAcronymName(AlumnoAssembler.toDto(duda.alumno).nombre));
        }
    }
}
