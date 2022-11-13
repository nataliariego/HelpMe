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
import com.example.helpme.model.Asignatura;

import java.util.ArrayList;
import java.util.List;

import assembler.AlumnoAssembler;
import dto.AlumnoDto;
import dto.DudaDto;
import util.StringUtils;

public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder> {

    private List<AlumnoDto> alumnos = new ArrayList<>();
//    private final AdapterView.OnItemClickListener listener;

    public AlumnoAdapter(List<AlumnoDto> alumnos) {
        this.alumnos = alumnos;
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_alumno, parent, false);
        return new AlumnoViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        AlumnoDto alumno = alumnos.get(position);

        holder.nombreAlumno.setText(alumno.nombre);

        holder.bindAlumno(alumno);
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    protected class AlumnoViewHolder extends RecyclerView.ViewHolder {

        private TextView nombreAlumno;
        private TextView asiganturas;
        private ImageView imagen_alumno;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreAlumno = itemView.findViewById(R.id.nombreAlumno);
            asiganturas = itemView.findViewById(R.id.textViewAsignaturasDominadas);
            imagen_alumno = itemView.findViewById(R.id.imageViewPerfil);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindAlumno(final AlumnoDto alumno) {
            nombreAlumno.setText(alumno.nombre);
            asiganturas.setText("hola");

            //fechaPublicacion.setText(DateUtils.prettyDate(duda.fecha));
        }
    }
}
