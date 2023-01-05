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
import com.example.helpme.model.Alumno;
import com.example.helpme.model.Materia;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import assembler.AlumnoAssembler;
import dto.DudaDto;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import util.DateUtils;
import util.StringUtils;

public class DudaAdapter extends RecyclerView.Adapter<DudaAdapter.DudaViewHolder> {

    public static final String TAG = "DUDA_ADAPTER";

    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(DudaDto item);
    }

    private List<DudaDto> dudas = new ArrayList<>();
    private final OnItemClickListener listener;

    public DudaAdapter(List<DudaDto> dudas, OnItemClickListener listener
    ) {
        this.dudas = dudas;
        this.listener = listener;
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

        holder.bindDuda(duda, listener);
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

        private ImageView imgPerfilAlumno;


        public DudaViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txTituloDuda);
            nombreAlumno = itemView.findViewById(R.id.txNombreAlumnoDudaResumen);
            fechaPublicacion = itemView.findViewById(R.id.txResumenFechaPublicacionDuda);
            abrevMateria = itemView.findViewById(R.id.txResumenDudaAbrevMateria);
            imgPerfilAlumno = itemView.findViewById(R.id.img_perfil_alumno_duda);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindDuda(final DudaDto duda, final OnItemClickListener listener) {
            titulo.setText(duda.titulo);
            fechaPublicacion.setText(DateUtils.prettyDate(duda.fecha, 1));

            if (duda.materia != null) {
                String abrev = duda.materia.get(Materia.ABREVIATURA).toString();
                abrevMateria.setText(abrev);
            }

            // https://github.com/wasabeef/picasso-transformations
            // Profile image
            String tempProfileImg = duda.alumno.get(Alumno.URL_FOTO) == null
                    ? "https://ui-avatars.com/api/?name=" + duda.alumno.get(Alumno.NOMBRE)
                    : duda.alumno.get(Alumno.URL_FOTO).toString();

            Picasso.get().load(tempProfileImg)
                    .fit()
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(imgPerfilAlumno);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(duda);
                }
            });


        }
    }
}
