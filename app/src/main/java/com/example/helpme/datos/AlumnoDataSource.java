package com.example.helpme.datos;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.helpme.model.Alumno;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AlumnoDataSource {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Alumno getAlumnoById(String toString) {
        final Object[][] alumno_data = new Object[1][1];
        DocumentReference docRef = db.collection("ALUMNO").document(toString);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        alumno_data[0] = document.getData().values().toArray();
                        Log.d(TAG, "---------DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //String id_curso = alumno_data[0][0].toString();
        //String id_materia = alumno_data[0][1].toString();
        //String nombre = alumno_data[0][2].toString();
        return new Alumno("DD", "JJ", "JJ");
    }
}
