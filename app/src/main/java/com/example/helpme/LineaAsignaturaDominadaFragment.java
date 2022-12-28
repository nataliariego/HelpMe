package com.example.helpme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import dto.AsignaturaDto;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LineaAsignaturaDominadaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LineaAsignaturaDominadaFragment extends Fragment {
    public static final String TAG = "LINEA_ASIGNATURA_DOM";

    private static final String DATA_ASIGNATURA = "DATA_ASIGNATURA";

    private AsignaturaDto asignatura;

    private TextView txNombreAsignaturaDominada;

    public LineaAsignaturaDominadaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LineaAsignaturaDominadaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LineaAsignaturaDominadaFragment newInstance(String param1) {
        LineaAsignaturaDominadaFragment fragment = new LineaAsignaturaDominadaFragment();
        Bundle args = new Bundle();
        args.putString(DATA_ASIGNATURA, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            asignatura = (AsignaturaDto) getArguments().get(DATA_ASIGNATURA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_linea_asignatura_dominada, container, false);
        initFields(rootView);

        return rootView;
    }

    private void initFields(View view) {
        txNombreAsignaturaDominada = (TextView) view.findViewById(R.id.text_asignatura_dominada);
    }
}