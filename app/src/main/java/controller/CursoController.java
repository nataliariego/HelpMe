package controller;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursoController {

    public static final String TAG = "CURSO-CONTROLLER";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static CursoController instance;

    public static synchronized CursoController getInstance() {
        if (instance == null) {
            instance = new CursoController();
            db = FirebaseFirestore.getInstance();
        }

        return instance;
    }

    /**
     * Listado de todas las dudas de la aplicación.
     *
     * @return
     */
    public MutableLiveData<List<Curso>> findAll() {
        MutableLiveData<List<Curso>> liveAsign = new MutableLiveData<List<Curso>>();

        db.collection("CURSO")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    List<Curso> cursos = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                           // Log.i("---> " , String.valueOf(documentSnapshot.get(Asignatura.CURSO).toString().split("=")[1].charAt(0)));

                            Curso c = new Curso();

                            c.setId(documentSnapshot.getId());
                            c.setNumero(documentSnapshot.getString(Curso.NUMERO));
                            //documentSnapshot.get(Duda.ASIGNATURA_REF).toString()


                            cursos.add(c);

                            System.out.println(c.toString());
                        }
                    }
                    liveAsign.postValue(cursos);

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
    public Optional<Curso> findById(DocumentReference ref) {
        Task<DocumentSnapshot> document = db.document(ref.getPath()).get();

        document.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot a = task.getResult();



                // CREAR c
            }
        });

        return Optional.ofNullable(null);
    }





}
