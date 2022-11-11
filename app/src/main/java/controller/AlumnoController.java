package controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.helpme.model.Alumno;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import assembler.AlumnoAssembler;
import dto.AlumnoDto;

public class AlumnoController {

    public static final String TAG = "ALUMNO_CONTROLLER";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    public void update(AlumnoDto alumno, String uid){

        Map<String, Object> alHash = AlumnoAssembler.toHashMap(alumno);
        db.collection(Alumno.COLLECTION).document(uid).set(alHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Alumno creado");
                }
            }
        });
    }

    private Alumno getPayload(String id, String uo, String nombre) {
        return new Alumno(id, uo, nombre);
    }

    public interface AlumnoCallback {
        void callback(Alumno alumno);
    }
}
