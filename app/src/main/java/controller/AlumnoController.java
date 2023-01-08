package controller;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Alumno;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dto.AlumnoDto;

public class AlumnoController {

    public static final String TAG = "ALUMNO_CONTROLLER";

    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static AlumnoController instance;

    public static synchronized AlumnoController getInstance() {
        if (instance == null) {
            instance = new AlumnoController();
            db = FirebaseFirestore.getInstance();
        }

        return instance;
    }

    /**
     * Listado de todas las dudas de la aplicación.
     */
    public MutableLiveData<List<Alumno>> findAll() {
        MutableLiveData<List<Alumno>> liveAlumnos = new MutableLiveData<>();

        db.collection(Alumno.COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Alumno> alumnos = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Alumno alumno = documentSnapshot.toObject(Alumno.class);

//                            AlumnoDto aRes = AlumnoAssembler.toDto(documentSnapshot.get(Duda.REF_ALUMNO).toString());
//                            Log.i(TAG, "ALUMNO CONTROLLER: " + aRes.nombre + " " + aRes.uo);

                            assert alumno != null;
                            alumno.setNombre(documentSnapshot.getString(Alumno.NOMBRE));
                            alumno.setUo(documentSnapshot.getString(Alumno.UO));
                            alumno.setUrl_foto(documentSnapshot.getString(Alumno.URL_FOTO));
                            alumno.setAsignaturasDominadas(new HashMap<>());

                            alumnos.add(alumno);
                        }
                    }
                    liveAlumnos.postValue(alumnos);

                });


        return liveAlumnos;
    }

    public MutableLiveData<List<Alumno>> findAllFriendsActivity() {
        MutableLiveData<List<Alumno>> liveAlumnos = new MutableLiveData<>();

        db.collection(Alumno.COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Alumno> alumnos = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            Alumno alumno = documentSnapshot.toObject(Alumno.class);

//                            AlumnoDto aRes = AlumnoAssembler.toDto(documentSnapshot.get(Duda.REF_ALUMNO).toString());
//                            Log.i(TAG, "ALUMNO CONTROLLER: " + aRes.nombre + " " + aRes.uo);

                            assert alumno != null;
                            alumno.setNombre(documentSnapshot.getString(Alumno.NOMBRE));
                            alumno.setUo(documentSnapshot.getString(Alumno.UO));
                            alumno.setUrl_foto(documentSnapshot.getString(Alumno.URL_FOTO));
                            alumno.setAsignaturasDominadas((Map<String, Object>) documentSnapshot.get("asignaturasDominadas"));
                            alumno.setId(documentSnapshot.getId());

                            alumnos.add(alumno);
                        }
                    }
                    liveAlumnos.postValue(alumnos);

                });


        return liveAlumnos;
    }


    /**
     * Obtiene un alumno por su referencia.
     */
    public void findById(DocumentReference ref, AlumnoCallback callback) {
        Task<DocumentSnapshot> document = db.document(ref.getPath()).get();

        document.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = document.getResult();

                Alumno alumno = getPayload(doc.getId(), doc.getString(Alumno.UO), doc.getString(Alumno.NOMBRE));

                callback.callback(alumno);
            }
        });
    }

    public void findByUO(String uo, AlumnoCallback callback) {
        Task<QuerySnapshot> document = db.collection(Alumno.COLLECTION).whereEqualTo("uo", uo).get();


        document.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();

                if (docs.size() > 0) {
                    DocumentSnapshot doc = docs.get(0);

                    Alumno alumno = getPayload(doc.getId(), doc.getString(Alumno.UO), doc.getString(Alumno.NOMBRE));
                    callback.callback(alumno);
                }
            }
        });
    }


    public void findByUOWithPhoto(String email, AlumnoCallback callback) {
        Task<QuerySnapshot> document = db.collection(Alumno.COLLECTION).whereEqualTo("email", email).get();

        document.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();

                Log.i(TAG, "DOCS: " + docs);

                if (docs.size() > 0) {
                    DocumentSnapshot doc = docs.get(0);

                    Alumno alumno = getPayloadWithUrl(doc.getId(), doc.getString(Alumno.UO), doc.getString(Alumno.NOMBRE), doc.getString(Alumno.URL_FOTO));
                    //alumno.setUo(doc.getString(Alumno.UO));
                    alumno.setEmail(doc.getString(Alumno.EMAIL));
                    alumno.setAsignaturasDominadas((Map<String, Object>) doc.get(Alumno.ASIGNATURAS_DOMINADAS));
                    alumno.setId(doc.getId());
                    alumno.setUrl_foto(doc.getString(Alumno.URL_FOTO));

                    Log.i(TAG, "USUARIO EN SESION: " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                    Log.i(TAG, "USER SES METADATA: " + FirebaseAuth.getInstance().getCurrentUser().getMetadata());
                    Log.i(TAG, "FOTO: " + doc.getString(Alumno.URL_FOTO));

                    callback.callback(alumno);
                }
            }
        });
    }

    public void update(AlumnoDto alumno, String uid) {
        alumno.password = "";

        db.collection(Alumno.COLLECTION).document(uid).set(alumno).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Alumno actualizado");
            }
        });
    }

    private Alumno getPayload(final String id, final String uo, final String nombre) {
        return new Alumno(id, uo, nombre);
    }

    private Alumno getPayloadWithUrl(final String id, final String uo, final String nombre, final String url_foto) {
        return new Alumno(id, uo, nombre, url_foto);
    }

    public interface AlumnoCallback {
        void callback(Alumno alumno);
    }
}
