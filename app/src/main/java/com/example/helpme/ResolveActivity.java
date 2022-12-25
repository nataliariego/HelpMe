package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.helpme.model.Duda;

public class ResolveActivity extends AppCompatActivity {

    private Duda duda;
    private TextView titulo;
    private TextView descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve);

        Intent intentDuda= getIntent();
        duda= intentDuda.getParcelableExtra(ListarDudasActivity.DUDA_SELECCIONADA);

        titulo = findViewById(R.id.tituloDuda);
        descripcion = findViewById(R.id.textDescripcion);

        titulo.setText(duda.getTitulo());
        System.out.println(duda.getDescripcion());
        descripcion.setText(duda.getDescripcion());

    }
}