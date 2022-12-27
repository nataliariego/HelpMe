package com.example.helpme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.model.Alumno;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import auth.Authentication;
import controller.AlumnoController;
import controller.AsignaturaController;
import controller.callback.GenericCallback;
import dto.AlumnoDto;
import dto.AsignaturaDto;
import util.FormValidator;
import viewmodel.AsignaturaViewModel;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String TAG = "CREATE_ACCOUNT_ACTIVITY";

    private EditText txEmail;
    private EditText txCompleteName;
    private EditText txUo;
    private TextInputEditText txPassword;
    private TextInputEditText txRepeatPassword;

    private AutoCompleteTextView txSelectorAsignaturasDominadas;
    private Button btAddAsignatura;

    private Button btVerAsignaturasDominadasSeleccionadas;

    private Button btCreateAccount;
    private Button btRedirectToLogin;

    private static AlumnoController alumnoController = new AlumnoController();

    private AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();

    private List<AsignaturaDto> asignaturasDisponibles = new ArrayList<>();

    private HashMap<String, Object> asignaturasDominadasSeleccionadas = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setTitle("Crear una cuenta");

        initFields();

        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        btRedirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToLogin();
            }
        });

    }

    private void initFields() {
        txUo = (EditText) findViewById(R.id.text_uo_create_account);
        txEmail = (EditText) findViewById(R.id.text_email_create_account);
        txCompleteName = (EditText) findViewById(R.id.text_nombre_completo_create_account);
        txPassword = (TextInputEditText) findViewById(R.id.text_password_ca);
        txRepeatPassword = (TextInputEditText) findViewById(R.id.text_repeat_password_create_account);
        btCreateAccount = (Button) findViewById(R.id.button_signup_create_account);
        btRedirectToLogin = (Button) findViewById(R.id.button_login_create_account);
        btVerAsignaturasDominadasSeleccionadas = (Button) findViewById(R.id.buttonVerAsignaturasSeleccionadasCreateAccount);

        txSelectorAsignaturasDominadas = (AutoCompleteTextView) findViewById(R.id.text_asignaturas_dominadas_create_account);
        btAddAsignatura = (Button) findViewById(R.id.button_add_asignatura_create_account);

        // Autocompletado para el textView de asignaturas
        ArrayAdapter asignaturasAutoCompleteAdapter = ArrayAdapter.createFromResource(this, R.array.asignaturas_array, android.R.layout.simple_spinner_dropdown_item);
        // TODO: Asignaturas sólamente disponibles
        txSelectorAsignaturasDominadas.setAdapter(asignaturasAutoCompleteAdapter);

        /* Evento click en boton añadir asignatura */
        btAddAsignatura.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (String.valueOf(txSelectorAsignaturasDominadas.getText()).trim().toLowerCase(Locale.ROOT) != "null" &&
                        !String.valueOf(txSelectorAsignaturasDominadas.getText()).isEmpty() &&
                        String.valueOf(txSelectorAsignaturasDominadas.getText()).trim() != "") {

                    String asigName = txSelectorAsignaturasDominadas.getText().toString();


                    AsignaturaController.getInstance().findByName(asigName, new AsignaturaController.AsignaturaCallback() {
                        @Override
                        public void callback(Map<String, Object> payload) {
                            int pos = asignaturasDominadasSeleccionadas.size() == 0 ? 1 : asignaturasDominadasSeleccionadas.size() - 1;
                            asignaturasDominadasSeleccionadas.put(String.valueOf(pos), payload);

                            Log.i(TAG, payload.toString());
                        }
                    });

                    txSelectorAsignaturasDominadas.setText("");

                }
            }
        });

        /* Evento click en ver asignaturas seleccionadas */
        btVerAsignaturasDominadasSeleccionadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsignaturasDominadasBottomSheetDialogFragment fragment = AsignaturasDominadasBottomSheetDialogFragment.newInstance(asignaturasDominadasSeleccionadas);
                fragment.show(getSupportFragmentManager(), fragment.getTag());

            }
        });
    }

    private void asignaturasDisponibles(){
        asignaturasDisponibles.clear();
        asignaturaViewModel.getAllAsignaturas().observe(this, asignaturas -> {
            if(asignaturas != null){
                asignaturas.forEach(a -> {
                    if(a != null){

                        AsignaturaDto asig = new AsignaturaDto();


                        //asignaturasDisponibles.add();
                    }
                });
            }
        });
    }

    /**
     * Redirecciona a la vista de Inicio de sesión.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void signUp() {
        if (validateFields()) {
            AlumnoDto alumno = new AlumnoDto();
            alumno.nombre = txCompleteName.getText().toString();
            alumno.uo = txUo.getText().toString();
            alumno.email = txEmail.getText().toString();
            alumno.password = txPassword.getText().toString();
            alumno.urlFoto = "https://ui-avatars.com/api/?name=" + alumno.nombre;
            alumno.asignaturasDominadas = asignaturasDominadasSeleccionadas;

            Log.i(TAG, "ALUMNO ANTES SIGNUP: " + alumno.toString());

            Authentication.getInstance().signUp(alumno, new GenericCallback<String>() {
                @Override
                public void callback(String msg) {
                    if (msg.equals(GenericCallback.SUCCESS_CODE)) {
                        redirectToHomeView();
                        Log.i(TAG, "SUCCESS");
                    } else {
                        Log.i(TAG, "ERROR");
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean validateFields() {

        String email = txEmail.getText().toString();
        String password = txPassword.getText().toString();
        String passwordRepeated = txRepeatPassword.getText().toString();
        String uo = txUo.getText().toString();

        if (!FormValidator.isNotEmpty(uo)) {
            txUo.setError(getText(R.string.uo_empty));
            return false;
        }

        alumnoController.findByUO(uo, new AlumnoController.AlumnoCallback() {
            @Override
            public void callback(Alumno alumno) {
                if (alumno != null) {
                    Toast.makeText(getApplicationContext(), getText(R.string.uo_already_exists), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        if (!FormValidator.isValidUOIdentifier(uo)) {
            txUo.setError(getText(R.string.uo_not_valid));
            return false;
        }

        if (!FormValidator.isNotEmpty(email)) {
            txEmail.setError(getText(R.string.email_empty));
            return false;
        }

        if (!FormValidator.isEmailValid(email)) {
            txEmail.setError(getText(R.string.email_invalid));
            return false;
        }

        if (!FormValidator.isNotEmpty(password)) {
            txPassword.setError(getText(R.string.password_empty));
            return false;
        }

        if (!FormValidator.isPasswordValid(password)) {
            txPassword.setError(getText(R.string.password_invalid));
            return false;
        }

        if (!FormValidator.passwordMatched(password, passwordRepeated)) {
            Toast.makeText(getApplicationContext(), getText(R.string.password_not_matching), Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    public void redirectToHomeView() {
        Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}