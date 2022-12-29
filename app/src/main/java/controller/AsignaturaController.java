package controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Asignatura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dto.AsignaturaDto;

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
                            Log.i("---> ", String.valueOf(documentSnapshot.get(Asignatura.CURSO).toString().split("=")[1].charAt(0)));

                            Asignatura asig = new Asignatura();

                            asig.setId(documentSnapshot.getId());
                            asig.setNombre(documentSnapshot.getString(Asignatura.NOMBRE));
                            //documentSnapshot.get(Duda.ASIGNATURA_REF).toString()
                            Log.i("---> ", documentSnapshot.get(Asignatura.CURSO).toString());
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
     * Parche. El curso y la materia de una asignatura se cargan como Mapa.
     *
     * @return
     * @since 28/12/2022
     */
    public MutableLiveData<List<Map<String, Object>>> findAllAsMap() {
        MutableLiveData<List<Map<String, Object>>> liveAsign = new MutableLiveData<List<Map<String, Object>>>();
        db.collection(Asignatura.COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Map<String, Object>> asignaturas = new ArrayList<>();

                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {

                            Map<String, Object> asig = new HashMap<>();
                            //asig.id = documentSnapshot.getId();

                            //Log.d(TAG, "ASIGNSSS: " + documentSnapshot.get(Asignatura.NOMBRE));
                            asig.put(Asignatura.NOMBRE, documentSnapshot.get(Asignatura.NOMBRE));
                            asig.put(Asignatura.CURSO, (Map<String, Object>) (Map<String, Object>) documentSnapshot.get(Asignatura.CURSO));
                            asig.put(Asignatura.MATERIA, (Map<String, Object>) documentSnapshot.get(Asignatura.MATERIA));

                            asignaturas.add(asig);
                        }
                    }
                    liveAsign.postValue(asignaturas);

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

    /**
     * Obtiene la asignatura con el nombre pasado como parámetro.
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findByName(String subjectName, AsignaturaCallback callback) {
        Task<QuerySnapshot> document = db.collection(Asignatura.COLLECTION)
                .whereEqualTo("nombre", subjectName).get();


        document.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();

                    if (docs.size() > 0) {
                        DocumentSnapshot doc = docs.get(0);
                        Map<String, Object> res = doc.getData();
                        res.put(Asignatura.ID, doc.getId());
                        callback.callback(res);
                    }
                }
            }
        });
    }

    public Asignatura getPayLoad(String nombre) {
        return new Asignatura(null, null, nombre);
    }

    public interface AsignaturaCallback {
        //void callback(Asignatura subject);

        // Ejemplo: {nombre=Comunicación Persona-Máquina, materia={abreviatura=CPM, denominacion=Interacción y Multimedia, id=eg8fSHgqSFd3ajoE7HmG}, curso={id=IvCCFH2OBBnMmmCvzZ0j, numero=2}}
        void callback(Map<String, Object> payload);
    }
}
