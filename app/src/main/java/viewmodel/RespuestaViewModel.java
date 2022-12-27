package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helpme.model.RespuestaDuda;

import java.util.List;

import controller.RespuestaController;


public class RespuestaViewModel extends ViewModel {

    private MutableLiveData<RespuestaDuda> respuesta;
    private MutableLiveData<List<RespuestaDuda>> respuestas;

    public RespuestaViewModel() {
    }

    public MutableLiveData<List<RespuestaDuda>> getAllRespuestas() {
        if (respuestas == null) {
            respuestas = RespuestaController.getInstance().findAll();
        }

        return respuestas;
    }

}
