package com.example.helpme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.databinding.FragmentAsignaturasDominadasBottomSheetDialogListDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.AsignaturaDominadaSheetAdapter;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     AsignaturasDominadasBottomSheetDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class AsignaturasDominadasBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "ASIG_DOM_SHEET_FRAGMENT";

    private RecyclerView recyclerAsignaturasDominadas;

    private AsignaturaDominadaSheetAdapter asignaturaDominadaSheetAdapter = new AsignaturaDominadaSheetAdapter(new ArrayList<>());

    Map<String, Object> asigs = new HashMap<>();

    // TODO: Customize parameter argument names
    private static final String ASIGNATURAS = "asignaturas_dominadas";
    private FragmentAsignaturasDominadasBottomSheetDialogListDialogBinding binding;

    // TODO: Customize parameters
    public static AsignaturasDominadasBottomSheetDialogFragment newInstance(Map<String, Object> asignaturas) {
        final AsignaturasDominadasBottomSheetDialogFragment fragment = new AsignaturasDominadasBottomSheetDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ASIGNATURAS, (Serializable) asignaturas);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAsignaturasDominadasBottomSheetDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public List<Map<String, Object>> getAsignaturasDominadas() {

        List<Map<String, Object>> res = new ArrayList<>();

        if(asignaturaDominadaSheetAdapter.getAsignaturasDominadas().size() == 0){
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
//        final ConstraintLayout layout = (ConstraintLayout) view;
//        layout.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setAdapter(new ItemAdapter(getArguments().get(ASIGNATURAS)));

        Bundle args = getArguments();

        List<String> nombresAsignaturas = new ArrayList<>();

        recyclerAsignaturasDominadas = (RecyclerView) view.findViewById(R.id.recycler_asignaturas_dominadas_sheet_createAccount);

        asigs = (Map<String, Object>) args.getSerializable(ASIGNATURAS);

        for (Map.Entry<String, Object> asignatura : asigs.entrySet()) {
            Map<String, Object> asig = (Map<String, Object>) asignatura.getValue();
            String nombre = asig.get("nombre").toString();
            nombresAsignaturas.add(nombre);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerAsignaturasDominadas.setLayoutManager(layoutManager);
        recyclerAsignaturasDominadas.setHasFixedSize(true);

        Log.i(TAG, "nombres: " + nombresAsignaturas.toString());
        asignaturaDominadaSheetAdapter = new AsignaturaDominadaSheetAdapter(nombresAsignaturas);
        recyclerAsignaturasDominadas.setAdapter(asignaturaDominadaSheetAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}