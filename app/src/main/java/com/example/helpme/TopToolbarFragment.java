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
 * Use the {@link TopToolbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopToolbarFragment extends Fragment {

    public static final String TAG = "TOP_TOOLBAR_FRAGMENT";

    private ImageButton btFaq;
    private ImageButton btBack;

    private ImageButton btSettings;

    private ImageView toolbarLogo;

    public TopToolbarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TopToolbarFragment.
     */
    public static TopToolbarFragment newInstance() {
        TopToolbarFragment fragment = new TopToolbarFragment();
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

        String activityName = getActivity().getClass().getSimpleName();

        Log.i(TAG, "Activity: " + activityName);

        btBack.setVisibility(View.INVISIBLE);
        toolbarLogo.setVisibility(View.VISIBLE);

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

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        Log.i(TAG, view.getClass().getName());
    }

    public ImageButton getBackButton() {
        return btBack;
    }
}