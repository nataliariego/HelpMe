package adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;

import java.util.ArrayList;
import java.util.List;

public class AsignaturaDominadaSheetAdapter extends RecyclerView.Adapter<AsignaturaDominadaSheetAdapter.AsignaturaDominadaViewHolder> {

    public static final String TAG = "ASIGNATURA_DOM_ADAPTER";

    private List<String> asignaturasDominadas = new ArrayList<>();

    public AsignaturaDominadaSheetAdapter(List<String> asignaturasDominadas) {
        this.asignaturasDominadas = asignaturasDominadas;
    }

    @NonNull
    @Override
    public AsignaturaDominadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_linea_asignatura_dominada, parent, false);

        return new AsignaturaDominadaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AsignaturaDominadaViewHolder holder, int position) {
        String asignatura = asignaturasDominadas.get(position);
        holder.bindAsignaturaDominada(asignatura);
    }

    public List<String> getAsignaturasDominadas(){
        return asignaturasDominadas;
    }

    @Override
    public int getItemCount() {
        return asignaturasDominadas.size();
    }

    protected class AsignaturaDominadaViewHolder extends RecyclerView.ViewHolder{

        private TextView txNombreAsignatura;
        private ImageButton btEliminarAsignatura;

        public AsignaturaDominadaViewHolder(@NonNull View itemView) {
            super(itemView);

            txNombreAsignatura = (TextView) itemView.findViewById(R.id.text_asignatura_dominada);
            btEliminarAsignatura = (ImageButton) itemView.findViewById(R.id.button_delete_asignaturaDominada);
        }

        public void bindAsignaturaDominada(final String nombre){
            txNombreAsignatura.setText(nombre);

            btEliminarAsignatura.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    asignaturasDominadas.remove(nombre);
                    notifyDataSetChanged();
                }
            });
        }

    }
}


