package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helpme.model.Asignatura;

import java.util.List;
import java.util.Map;

import controller.AsignaturaController;

public class AsignaturaViewModel extends ViewModel {

    private MutableLiveData<Asignatura> duda;
    private MutableLiveData<List<Asignatura>> dudas;

    public AsignaturaViewModel() {
    }

    public MutableLiveData<List<Asignatura>> getAllDudas() {
        if (dudas == null) {
            dudas = AsignaturaController.getInstance().findAll();
        }

        return dudas;
    }

    public MutableLiveData<List<Asignatura>> getAllAsignaturas() {
        return AsignaturaController.getInstance().findAll();
    }

    public MutableLiveData<List<Map<String, Object>>> getAllAsignaturasAsMap() {
        return AsignaturaController.getInstance().findAllAsMap();
    }
}
