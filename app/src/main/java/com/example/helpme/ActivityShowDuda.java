package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.helpme.model.Duda;

public class ActivityShowDuda extends AppCompatActivity {

    private Duda duda;

    private TextView titulo;
    private TextView persona;
    private TextView fecha;
    private TextView asignatura;
    private ImageButton persona_foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_duda);
        super.onCreate(savedInstanceState);

        //aqui iria intents de lo anterior

    }



}