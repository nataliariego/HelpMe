package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Duda;

import java.util.List;

import controller.AlumnoController;
import controller.DudaController;

public class AlumnoViewModel extends ViewModel {
    private MutableLiveData<Alumno> alumno;
    private MutableLiveData<List<Alumno>> alumnos;

    public AlumnoViewModel() {
    }

    public MutableLiveData<List<Alumno>> getAllAlumnos() {
        if (alumnos == null) {
            alumnos = AlumnoController.getInstance().findAll();
        }

        return alumnos;
    }

    public void addDuda(){

    }
}
