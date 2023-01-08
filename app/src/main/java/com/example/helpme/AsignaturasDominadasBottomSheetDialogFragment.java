package com.example.helpme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.databinding.FragmentAsignaturasDominadasBottomSheetDialogListDialogBinding;
import com.example.helpme.model.Asignatura;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import adapter.AsignaturaDominadaSheetAdapter;

/**
 * Activity (Bottom Sheet) que contiene las asignaturas seleccionadas en
 * el campo "Selecciona las asignaturas" del activity de Crear una Cuenta.
 * Al pulsar sobre el botón situado a la derecha del campo anterior, se añade la asignatura
 * a la lista de asignaturas dominadas y, pulsando el botón "Ver asignaturas seleccionadas",
 * se muestra esta activity.
 */
public class AsignaturasDominadasBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "ASIG_DOM_SHEET_FRAGMENT";
    private static final String ASIGNATURAS = "asignaturas_dominadas";

    private AsignaturaDominadaSheetAdapter asignaturaDominadaSheetAdapter = new AsignaturaDominadaSheetAdapter(new ArrayList<>());
    Map<String, Object> asigs = new HashMap<>();

    private FragmentAsignaturasDominadasBottomSheetDialogListDialogBinding binding;

    public static AsignaturasDominadasBottomSheetDialogFragment newInstance(Map<String, Object> asignaturas) {
        final AsignaturasDominadasBottomSheetDialogFragment fragment = new AsignaturasDominadasBottomSheetDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ASIGNATURAS, (Serializable) asignaturas);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAsignaturasDominadasBottomSheetDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public List<Map<String, Object>> getAsignaturasDominadas() {

        List<Map<String, Object>> res = new ArrayList<>();

        if (asignaturaDominadaSheetAdapter.getAsignaturasDominadas().size() == 0) {
            return new ArrayList<>();
        }

        for (String a : asignaturaDominadaSheetAdapter.getAsignaturasDominadas()) {
            Map<String, Object> asignatura = (Map<String, Object>) asigs.get(a);
            res.add(asignatura);
        }

        return res;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        List<String> nombresAsignaturas = new ArrayList<>();

        RecyclerView recyclerAsignaturasDominadas = (RecyclerView) view.findViewById(R.id.recycler_asignaturas_dominadas_sheet_createAccount);

        assert args != null;
        asigs = (Map<String, Object>) args.getSerializable(ASIGNATURAS);

        for (Map.Entry<String, Object> asignatura : asigs.entrySet()) {
            Map<String, Object> asig = (Map<String, Object>) asignatura.getValue();
            String nombre = Objects.requireNonNull(asig.get(Asignatura.NOMBRE)).toString();
            nombresAsignaturas.add(nombre);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerAsignaturasDominadas.setLayoutManager(layoutManager);
        recyclerAsignaturasDominadas.setHasFixedSize(true);

        asignaturaDominadaSheetAdapter = new AsignaturaDominadaSheetAdapter(nombresAsignaturas);
        recyclerAsignaturasDominadas.setAdapter(asignaturaDominadaSheetAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}