package controller;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Optional;

public class AsignaturaController {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
