package viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helpme.model.Duda;

import java.util.List;

import controller.DudaController;

public class DudaViewModel extends ViewModel {
    private MutableLiveData<Duda> duda;
    private MutableLiveData<List<Duda>> dudas;

    public DudaViewModel() {
    }

    public MutableLiveData<List<Duda>> getAllDudas() {
        if (dudas == null) {
            dudas = DudaController.getInstance().findAll();
        }

        return dudas;
    }

    public void addDuda(){

    }
}
