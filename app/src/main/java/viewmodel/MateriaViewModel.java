package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;
import com.example.helpme.model.Materia;

import java.util.List;

import controller.AsignaturaController;
import controller.CursoController;
import controller.MateriaController;

public class MateriaViewModel extends ViewModel {

    private MutableLiveData<Materia> duda;
    private MutableLiveData<List<Materia>> dudas;

    public MateriaViewModel() {
    }

    public MutableLiveData<List<Materia>> getAllDudas() {
        if (dudas == null) {

            dudas = MateriaController.getInstance().findAll();
        }

        return dudas;
    }
}
