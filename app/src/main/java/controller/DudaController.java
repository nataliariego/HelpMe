package controller;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Duda;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DudaController {

    public static final String TAG = "DUDAS_CONTROLLER";

    private static FirebaseFirestore db;
    private AlumnoController alumnoController = new AlumnoController();

    //    Campos de la base de datos

    private static DudaController instance;

    public static synchronized DudaController getInstance() {
        if (instance == null) {
            instance = new DudaController();
            db = FirebaseFirestore.getInstance();
        }

        return instance;
    }


    /**
     * Listado de todas las dudas de la aplicación.
     *
     * @return
     */
    public MutableLiveData<List<Duda>> findAll() {
        MutableLiveData<List<Duda>> liveDudas = new MutableLiveData<List<Duda>>();

        db.collection(Duda.COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Duda> dudas = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Duda duda = documentSnapshot.toObject(Duda.class);


                            duda.setTitulo(documentSnapshot.getString(Duda.TITULO));
                            duda.setDescripcion(documentSnapshot.getString(Duda.DESCRIPCION));
                            duda.setFecha(documentSnapshot.getString(Duda.FECHA));
                            duda.setResuelta(documentSnapshot.getBoolean(Duda.IS_RESUELTA));
                            duda.setAsignaturaId(documentSnapshot.get(Duda.ASIGNATURA_REF).toString());
                            duda.setAlumnoId(documentSnapshot.get(Duda.REF_ALUMNO).toString());

                            dudas.add(duda);
                        }
                    }
                    liveDudas.postValue(dudas);

                });


        return liveDudas;
    }
}
