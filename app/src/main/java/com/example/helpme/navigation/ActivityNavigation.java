package com.example.helpme.navigation;

import android.content.Context;

public interface ActivityNavigation {

    /**
     * Función de navegacion entre dos activities.
     *
     * @param context Origen - Contexto del activity.
     * @param toClass Destino - Clase
     */
    void go(Context context, Class toClass);
}
