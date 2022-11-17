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

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Materia;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import assembler.AlumnoAssembler;
import de.hdodenhof.circleimageview.CircleImageView;
import dto.DudaDto;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import util.StringUtils;

public class DudaAdapter extends RecyclerView.Adapter<DudaAdapter.DudaViewHolder> {

    public static final String TAG = "DUDA_ADAPTER";

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

        private ImageView imgPerfilAlumno;


        public DudaViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.txTituloDuda);
            nombreAlumno = itemView.findViewById(R.id.txNombreAlumnoDudaResumen);
            fechaPublicacion = itemView.findViewById(R.id.txResumenFechaPublicacionDuda);
            abrevMateria = itemView.findViewById(R.id.txResumenDudaAbrevMateria);
            siglasAlumno = itemView.findViewById(R.id.tx_siglas_alumno_avatar);

            imgPerfilAlumno = itemView.findViewById(R.id.img_perfil_alumno_duda);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bindDuda(final DudaDto duda) {
            titulo.setText(duda.titulo);
            //fechaPublicacion.setText(DateUtils.prettyDate(duda.fecha));
            fechaPublicacion.setText(duda.fecha);

            siglasAlumno.setText(StringUtils.getAcronymName(duda.alumno.get(Alumno.NOMBRE).toString()));

            System.out.println("--->"+duda.materia);
            if(duda.materia != null){
                String abrev = duda.materia.get(Materia.ABREVIATURA).toString();
                abrevMateria.setText(abrev);
                System.out.println("--->"+abrev);
            }


            if(!duda.alumno.get(Alumno.URL_FOTO).toString().isEmpty()){
                siglasAlumno.setVisibility(View.INVISIBLE);

                // https://github.com/wasabeef/picasso-transformations
                Picasso.get().load(duda.alumno.get(Alumno.URL_FOTO).toString())
                        .fit()
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .into(imgPerfilAlumno);
            }


        }
    }
}
