package com.example.helpme;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.squareup.picasso.Picasso;

import com.example.helpme.model.Duda;

import java.util.List;

public class ListaDudasAdapter extends RecyclerView.Adapter<ListaDudasAdapter.DudaViewHolder> {


    //interfaz para manejar el evento click sobre un elemento
    //para cada item del mainrecycler
    public interface OnItemClickListener {
        void onItemClick(Duda item);
    }


    private List<Duda> listaDudas;
    private final OnItemClickListener listener;

    public ListaDudasAdapter(List<Duda> listaPeliculas, OnItemClickListener listener) {
        this.listaDudas = listaPeliculas;
        this.listener = listener;
    }


    @NonNull
    @Override
    public DudaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_duda,parent,false);
        return new DudaViewHolder(itemView);
        //este metodo va poniendo los layouts uno detras del otro
    }

    @Override
    public void onBindViewHolder(@NonNull DudaViewHolder holder, int position) {
        Duda duda = listaDudas.get(position);
        holder.bindUser(duda,listener);
    }





    @Override
    public int getItemCount() {
        return listaDudas.size();
    }


    protected class DudaViewHolder extends RecyclerView.ViewHolder {
    private TextView titulo;
    private TextView persona_duda;
    private ImageView img_persona_duda;
    private TextView fecha;
    private TextView asignatura;

        public DudaViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.tituloDuda);
            persona_duda = (TextView) itemView.findViewById(R.id.persona_duda);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            asignatura = (TextView) itemView.findViewById(R.id.asignatura);
            img_persona_duda = (ImageView) itemView.findViewById(R.id.img_persona_duda);
        }


        // asignar valores a los componentes
    public void bindUser(final Duda duda, final OnItemClickListener listener) {
        titulo.setText(duda.getTitulo());
        persona_duda.setText(duda.getAlumno().getNombre());
        fecha.setText(duda.getCreatedAt().toString());
        asignatura.setText(duda.getAsignatura().getNombre());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(duda);
            }
        });
    }
}
}
