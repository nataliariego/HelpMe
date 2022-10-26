package com.example.helpme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.helpme.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Resetea los estilos del spashScreen
        //setTheme(R.style.Theme_HelpMe);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}