package controller;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.RespuestaDuda;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RespuestaController {

    public static final String TAG = "RESPUESTA_CONTROLLER";

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static RespuestaController instance;

    public static synchronized RespuestaController getInstance() {
        if (instance == null) {
            instance = new RespuestaController();
            db = FirebaseFirestore.getInstance();
        }

        return instance;
    }

    /**
     * Listado de todas las dudas de la aplicación.
     *
     * @return
     */
    public MutableLiveData<List<RespuestaDuda>> findAll() {
        MutableLiveData<List<RespuestaDuda>> liveRespuestas = new MutableLiveData<List<RespuestaDuda>>();

        db.collection(RespuestaDuda.COLLECTION)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<RespuestaDuda> respuestas = new ArrayList<>();
                    if (snapshot != null && !snapshot.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : snapshot.getDocuments()) {
                            RespuestaDuda res = documentSnapshot.toObject(RespuestaDuda.class);
                            res.setEmailDuda(documentSnapshot.getString(RespuestaDuda.ALUMNO_DUDA));
                            res.setEmailResponde(documentSnapshot.getString(RespuestaDuda.ALUMNO_RESPONDE));
                            res.setFecha(documentSnapshot.getString(RespuestaDuda.FECHA));
                            res.setIdDuda(documentSnapshot.getString(RespuestaDuda.ID_DUDA));
                            res.setDescripcion(documentSnapshot.getString(RespuestaDuda.RESPUESTA));

                            respuestas.add(res);
                        }
                    }
                    liveRespuestas.postValue(respuestas);

                });


        return liveRespuestas;
    }
}
