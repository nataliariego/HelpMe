package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private AnimationDrawable logoAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setTitle("Helpme: Cargando aplicación");

        ImageView imgLogo = (ImageView) findViewById(R.id.img_logo_splash_screen);
        imgLogo.setBackgroundResource(R.drawable.splash_screen_animation);
        logoAnimation = (AnimationDrawable) imgLogo.getBackground();
        logoAnimation.setOneShot(true);
        Handler handler= new Handler();

        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        logoAnimation.start();
    }
}