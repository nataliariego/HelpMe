package controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Duda;
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
import java.util.UUID;

import assembler.AlumnoAssembler;
import dto.AlumnoDto;

public class AlumnoController {

    public static final String TAG = "ALUMNO_CONTROLLER";

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
     *
     * @return
     */
    public MutableLiveData<List<Alumno>> findAll() {
        MutableLiveData<List<Alumno>> liveAlumnos = new MutableLiveData<List<Alumno>>();

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
        MutableLiveData<List<Alumno>> liveAlumnos = new MutableLiveData<List<Alumno>>();

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

                            alumno.setNombre(documentSnapshot.getString(Alumno.NOMBRE));
                            alumno.setUo(documentSnapshot.getString(Alumno.UO));
                            alumno.setUrl_foto(documentSnapshot.getString(Alumno.URL_FOTO));
                            alumno.setAsignaturasDominadas((Map<String,Object>)documentSnapshot.get("asignaturasDominadas"));

                            alumnos.add(alumno);
                        }
                    }
                    liveAlumnos.postValue(alumnos);

                });


        return liveAlumnos;
    }


    /**
     * Obtiene un alumno por su referencia.
     *
     * @param ref
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findById(DocumentReference ref, AlumnoCallback callback) {
        Task<DocumentSnapshot> document = db.document(ref.getPath()).get();

        document.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = document.getResult();

                    Alumno alumno = getPayload(doc.getId(), doc.getString(Alumno.UO), doc.getString(Alumno.NOMBRE));

                    callback.callback(alumno);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findByUO(String uo, AlumnoCallback callback) {
        Task<QuerySnapshot> document = db.collection(Alumno.COLLECTION).whereEqualTo("uo", uo).get();



        document.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();

                    if(docs.size() > 0){
                        DocumentSnapshot doc = docs.get(0);

                        Alumno alumno = getPayload(doc.getId(), doc.getString(Alumno.UO), doc.getString(Alumno.NOMBRE));
                        callback.callback(alumno);
                    }
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void findByUOWithPhoto(String uo, AlumnoCallback callback) {
        Task<QuerySnapshot> document = db.collection(Alumno.COLLECTION).whereEqualTo("uo", uo).get();



        document.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();

                    if(docs.size() > 0){
                        DocumentSnapshot doc = docs.get(0);

                        Alumno alumno = getPayloadWithUrl(doc.getId(), doc.getString(Alumno.UO), doc.getString(Alumno.NOMBRE), doc.getString(Alumno.URL_FOTO));
                        //alumno.setUo(doc.getString(Alumno.UO));
                        alumno.setEmail(doc.getString("email"));
                        alumno.setAsignaturasDominadas((Map<String,Object>)doc.get("asignaturasDominadas"));
                        alumno.setId(doc.getId());
                        callback.callback(alumno);
                    }
                }
            }
        });
    }

    public void update(AlumnoDto alumno, String uid){

        Map<String, Object> alHash = AlumnoAssembler.toHashMap(alumno);
        db.collection(Alumno.COLLECTION).document(uid).set(alHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Alumno actualizado");
                }
            }
        });
    }

    private Alumno getPayload(String id, String uo, String nombre) {
        return new Alumno(id, uo, nombre);
    }

    private Alumno getPayloadWithUrl(String id, String uo, String nombre, String url_foto) {
        return new Alumno(id, uo, nombre, url_foto);
    }

    public interface AlumnoCallback {
        void callback(Alumno alumno);
    }
}
