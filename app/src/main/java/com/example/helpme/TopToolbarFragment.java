package com.example.helpme;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopToolbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopToolbarFragment extends Fragment {

    public static final String TAG = "TOP_TOOLBAR_FRAGMENT";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton btFaq;
    private ImageButton btBack;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopToolbarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopToolbarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopToolbarFragment newInstance(String param1, String param2) {
        TopToolbarFragment fragment = new TopToolbarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_toolbar, container, false);
        initElements(view);
        return view;
    }

    private void initElements(View view){
        btBack = view.findViewById(R.id.button_toolbar_back);
        btFaq = view.findViewById(R.id.button_toolbar_faq);

        btFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FaqActivity.class);
                startActivity(intent);
            }
        });
    }
}