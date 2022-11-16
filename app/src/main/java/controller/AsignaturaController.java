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

import org.checkerframework.checker.units.qual.A;

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
        MutableLiveData<List<Asignatura>> liveAsign = new MutableLiveData<List<Asignatura>>();

        db.collection("ASIGNATURA")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    List<Asignatura> asignasturas = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Log.i("---> " , String.valueOf(documentSnapshot.get(Asignatura.CURSO).toString().split("=")[1].charAt(0)));

                            Asignatura asig = new Asignatura();

                            asig.setId(documentSnapshot.getId());
                            asig.setNombre(documentSnapshot.getString(Asignatura.NOMBRE));
                            //documentSnapshot.get(Duda.ASIGNATURA_REF).toString()
                            Log.i("---> " ,documentSnapshot.get(Asignatura.CURSO).toString());
                            asig.setCurso(documentSnapshot.get(Asignatura.CURSO).toString());
                            asig.setMateria(documentSnapshot.get(Asignatura.MATERIA).toString());

                            asignasturas.add(asig);
                            
                            System.out.println(asig.toString());
                        }
                    }
                    liveAsign.postValue(asignasturas);

                });


        return liveAsign;
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
