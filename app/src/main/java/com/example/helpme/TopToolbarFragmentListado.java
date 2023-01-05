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
 * A simple {@link Fragment} subclass.
 * Use the {@link TopToolbarFragmentListado#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopToolbarFragmentListado extends Fragment {

    public static final String TAG = "TOP_TOOLBAR_FRAGMENT";

    private ImageButton btFaq;
    private ImageButton btSettings;
    private ImageView toolbarLogo;

    public TopToolbarFragmentListado() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TopToolbarFragment.
     */
    public static TopToolbarFragmentListado newInstance() {
        TopToolbarFragmentListado fragment = new TopToolbarFragmentListado();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_toolbar_listado, container, false);
        initElements(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void initElements(View view) {
        btFaq = (ImageButton) view.findViewById(R.id.button_toolbar_faq);
        toolbarLogo = (ImageView) view.findViewById(R.id.toolbar_logo);

        btSettings = (ImageButton) view.findViewById(R.id.button_toolbar_settings);

        String activityName = getActivity().getClass().getSimpleName();

        // En la vista de Home mostrar el logo en vez del botón de volver
        if (activityName.equalsIgnoreCase("HomeActivity")) {
            toolbarLogo.setVisibility(View.VISIBLE);
        }

        btSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AjustesCuentaActivity.class);
                startActivity(intent);
            }
        });

        btFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FaqActivity.class);
                startActivity(intent);
            }
        });

        Log.i(TAG, view.getClass().getName());
    }


}