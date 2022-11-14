package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;

import java.util.List;

import controller.AsignaturaController;
import controller.CursoController;

public class CursoViewModel extends ViewModel {

    private MutableLiveData<Curso> curso;
    private MutableLiveData<List<Curso>> cursos;


    public CursoViewModel() {
    }

    public MutableLiveData<List<Curso>> getAllCursos() {
        if (cursos == null) {
            cursos = CursoController.getInstance().findAll();
        }

        return cursos;
    }
}
