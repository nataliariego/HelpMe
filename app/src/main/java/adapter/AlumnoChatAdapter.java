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

import java.util.List;

import dto.AlumnoDto;

public class AlumnoChatAdapter extends RecyclerView.Adapter<AlumnoChatAdapter.AlumnoChatViewHolder> {

    public interface OnClickListener {
        void goToChat(AlumnoDto alumno);
    }

    private List<AlumnoDto> alumnos;
    private OnClickListener listener;

    public AlumnoChatAdapter(List<AlumnoDto> alumnos, OnClickListener listener) {
        this.alumnos = alumnos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlumnoChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_alumno_chat, parent, false);

        return new AlumnoChatAdapter.AlumnoChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoChatViewHolder holder, int position) {
        AlumnoDto alumno = alumnos.get(position);
        holder.bindAlumnoChat(alumno);
    }

    @Override
    public int getItemCount() {
        return alumnos != null ? alumnos.size() : 0;
    }

    public class AlumnoChatViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPerfilAlumno;
        private TextView txNombreAlumno;
        private TextView txUoAlumno;

        public AlumnoChatViewHolder(@NonNull View itemView) {
            super(itemView);

            txUoAlumno = itemView.findViewById(R.id.text_uo_alumno_chat);
            txNombreAlumno = itemView.findViewById(R.id.text_nombre_alumno_chat);
            imgPerfilAlumno = itemView.findViewById(R.id.img_avatar_alumno_chat);
        }

        public void bindAlumnoChat(AlumnoDto alumno) {
            txUoAlumno.setText(alumno.uo);
            txNombreAlumno.setText(alumno.nombre);

            String profileImgUrl = alumno.urlFoto == null
                    ? "https://ui-avatars.com/api/?name=" + String.join(alumno.nombre)
                    : alumno.urlFoto;

            Picasso.get().load(profileImgUrl).into(imgPerfilAlumno);

            /* Conversación (Nuevo chat) con el alumno seleccionado */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.goToChat(alumno);
                }
            });

        }
    }
}
