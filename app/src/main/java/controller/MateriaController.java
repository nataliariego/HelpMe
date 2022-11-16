package controller;

import android.os.Build;
import android.util.Log;


import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Materia;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class MateriaController {

    public static final String TAG = "MATERIA-CONTROLLER";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static MateriaController instance;

    public static synchronized MateriaController getInstance() {
        if (instance == null) {
            instance = new MateriaController();
            db = FirebaseFirestore.getInstance();
        }

        return instance;
    }

    /**
     * Listado de todas las dudas de la aplicación.
     *
     * @return
     */
    public MutableLiveData<List<Materia>> findAll() {
        MutableLiveData<List<Materia>> liveAsign = new MutableLiveData<List<Materia>>();

        db.collection(Materia.COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    System.out.println("Holasaaassssdsfdrg");
                    List<Materia> cursos = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            // Log.i("---> " , String.valueOf(documentSnapshot.get(Asignatura.CURSO).toString().split("=")[1].charAt(0)));

                            Materia c = new Materia();

                            c.setId(documentSnapshot.getId());
                            c.setAbreviatura(documentSnapshot.getString(Materia.ABREVIATURA));
                            c.setDenominacion(documentSnapshot.getString(Materia.DENOMINACION));
                            //documentSnapshot.get(Duda.ASIGNATURA_REF).toString()


                            cursos.add(c);

                            System.out.println(c.toString());
                        }
                    }
                    liveAsign.postValue(cursos);

                });


        return liveAsign;
    }

}
