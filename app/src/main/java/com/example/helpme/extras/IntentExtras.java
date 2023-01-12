package com.example.helpme.extras;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.example.helpme.HomeActivity;
import com.example.helpme.ListarChatsActivity;
import com.example.helpme.ListarDudasActivity;
import com.example.helpme.ProfileActivity;
import com.example.helpme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class IntentExtras {

    public static final String CHAT_SELECCIONADO = "chat_seleccionado";

    public static IntentExtras instance;

    /**
     * Redirección entre activities.
     *
     * @param currentContext           Contexto actual del activity de origen.
     * @param destinationActivityClass Clase del Activity destino.
     */
    private void redirectTo(Context currentContext, Class destinationActivityClass) {
        Intent e = new Intent(currentContext, destinationActivityClass);
        e.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentContext.startActivity(e);
    }

    /**
     * Manejador de las transiciones entre Activities del bottom navigation menu de la
     * aplicación.
     *
     * @param navegacion     Elemento BottomNavigationView.
     * @param currentContext Contexto del activity origen.
     */
    @SuppressLint("NonConstantResourceId")
    public void handleNavigationView(BottomNavigationView navegacion, Context currentContext) {
        navegacion.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.nav_home:
                    IntentExtras.getInstance().redirectTo(currentContext, HomeActivity.class);
                    return true;

                case R.id.nav_chat:
                    IntentExtras.getInstance().redirectTo(currentContext, ListarChatsActivity.class);
                    return true;

                case R.id.nav_cuenta:
                    IntentExtras.getInstance().redirectTo(currentContext, ProfileActivity.class);
                    return true;

                case R.id.nav_dudas:
                    IntentExtras.getInstance().redirectTo(currentContext, ListarDudasActivity.class);
                    return true;
            }
            return false;
        });
    }

    public static IntentExtras getInstance() {
        if (instance == null) {
            instance = new IntentExtras();
        }
        return instance;
    }
}
