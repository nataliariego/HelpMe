package viewModel;

import androidx.lifecycle.MutableLiveData;

import com.example.helpme.model.Asignatura;

import java.util.List;

import controller.AsignaturaController;

public class AsignaturaViewModel {

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

}
