package com.example.helpme.datos;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.helpme.model.Asignatura;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.*;

public class AsignaturaDataSource {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Object[] asignatura_data;





    public Asignatura getAsignaturaById(String toString) {

        DocumentReference docRef = db.collection("ASIGNATURA").document(toString);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        asignatura_data = document.getData().values().toArray();
                        Log.d(TAG, "--------->DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        System.out.println("*******" + asignatura_data);
       /* String id_curso = asignatura_data[0][0].toString();
        String id_materia = asignatura_data[0][1].toString();
        String nombre = asignatura_data[0][2].toString();*/
        //cambio constructor Asignatura
        //return new Asignatura("d", "s", "s");ç
        return null;
    }


}
