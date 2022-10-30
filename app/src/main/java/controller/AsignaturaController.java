package controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsignaturaController {

    public static final String TAG = "ASIGNATURA-CONTROLLER";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static AsignaturaController instance;

    public static synchronized AsignaturaController getInstance() {
        if (instance == null) {
            instance = new AsignaturaController();
            db = FirebaseFirestore.getInstance();
        }

        return instance;
    }

    /**
     * Listado de todas las dudas de la aplicación.
     *
     * @return
     */
    public MutableLiveData<List<Asignatura>> findAll() {
        MutableLiveData<List<Asignatura>> liveDudas = new MutableLiveData<List<Asignatura>>();

        db.collection("ASIGNATURA")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Asignatura> dudas = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Asignatura asig = documentSnapshot.toObject(Asignatura.class);

/*
                            asig.setNombre(documentSnapshot.getString(Asignatura.NOMBRE));
                            duda.setDescripcion(documentSnapshot.getString(Duda.DESCRIPCION));
                            duda.setFecha(documentSnapshot.getString(Duda.FECHA));
                            duda.setResuelta(documentSnapshot.getBoolean(Duda.IS_RESUELTA));
                            duda.setAsignaturaId(documentSnapshot.get(Duda.ASIGNATURA_REF).toString());
                            duda.setAlumnoId(documentSnapshot.get(Duda.REF_ALUMNO).toString());

                            dudas.add(duda);
                            
 */
                        }
                    }
                    liveDudas.postValue(dudas);

                });


        return liveDudas;
    }

    /**
     * Obtiene un alumno por su referencia.
     *
     * @param ref
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Optional<Asignatura> findById(DocumentReference ref) {
        Task<DocumentSnapshot> document = db.document(ref.getPath()).get();

        document.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot a = task.getResult();



                // CREAR asignatura
            }
        });

        return Optional.ofNullable(null);
    }
}
