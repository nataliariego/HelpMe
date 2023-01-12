package com.example.helpme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Toolbar superior de la aplicación que contiene un botón para volver a la activity
 * anterior, un botón de ajustes que redirecciona a la vista de ajustes generales de la
 * aplicación y un botón para redireccionar a la vista de acerca.
 *
 * @author UO257239
 * @version v1.0.1
 */
public class TopToolbarFragment extends Fragment {

    public static final String TAG = "TOP_TOOLBAR_FRAGMENT";

    private ImageButton btBack;
    private ImageButton btSettings;
    private ImageButton btFaq;
    private ImageView toolbarLogo;

    public TopToolbarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_toolbar, container, false);
        initElements(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void initElements(View view) {
        btBack = (ImageButton) view.findViewById(R.id.button_toolbar_back);
        btFaq = (ImageButton) view.findViewById(R.id.button_toolbar_faq);
        toolbarLogo = (ImageView) view.findViewById(R.id.toolbar_logo);
        btSettings = (ImageButton) view.findViewById(R.id.button_toolbar_settings);
        String activityName = requireActivity().getClass().getSimpleName();

        if (activityName.equalsIgnoreCase(HomeActivity.class.getSimpleName())) {
            btBack.setVisibility(View.GONE);
            toolbarLogo.setVisibility(View.VISIBLE);
        } else {
            toolbarLogo.setVisibility(View.GONE);
            btBack.setVisibility(View.VISIBLE);
        }

        addListeners();
        Log.i(TAG, "Activity: " + activityName);
    }

    private void addListeners() {
        /* Redireccion a la activity de ajustes */
        btSettings.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), AjustesCuentaActivity.class);
            startActivity(intent);
        });
        /* Redireccion a la activity de acerca-de */
        btFaq.setOnClickListener(view12 -> {
            Intent intent = new Intent(view12.getContext(), FaqActivity.class);
            startActivity(intent);
        });

        toolbarLogo.setOnClickListener(view -> startActivity(new Intent(view.getContext(), HomeActivity.class)));

        /* Volver a la activity anterior */
        btBack.setOnClickListener(view13 -> System.exit(0));
    }

}