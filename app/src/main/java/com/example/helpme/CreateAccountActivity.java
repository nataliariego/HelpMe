package com.example.helpme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpme.model.Alumno;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import auth.Authentication;
import controller.AlumnoController;
import controller.callback.GenericCallback;
import dto.AlumnoDto;
import util.FormValidator;

public class CreateAccountActivity extends AppCompatActivity {
    public static final String TAG = "CREATE_ACCOUNT_ACTIVITY";

    private EditText txEmail;
    private EditText txCompleteName;
    private EditText txUo;
    private TextInputEditText txPassword;
    private TextInputEditText txRepeatPassword;
    private AutoCompleteTextView txSelectorAsignaturasDominadas;
    private Button btAddAsignatura;

    private Button btCreateAccount;
    private Button btRedirectToLogin;

    private static AlumnoController alumnoController = new AlumnoController();

    private Set<String> asignaturasDominadasSeleccionadas = new HashSet<>();

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
        asignaturasDominadasSeleccionadas.clear();

        txUo = (EditText) findViewById(R.id.text_uo_create_account);
        txEmail = (EditText) findViewById(R.id.text_email_create_account);
        txCompleteName = (EditText) findViewById(R.id.text_nombre_completo_create_account);
        txPassword = (TextInputEditText) findViewById(R.id.text_password_ca);
        txRepeatPassword = (TextInputEditText) findViewById(R.id.text_repeat_password_create_account);
        btCreateAccount = (Button) findViewById(R.id.button_signup_create_account);
        btRedirectToLogin = (Button) findViewById(R.id.button_login_create_account);
        txSelectorAsignaturasDominadas = (AutoCompleteTextView) findViewById(R.id.text_asignaturas_dominadas_create_account);
        btAddAsignatura = (Button) findViewById(R.id.button_add_asignatura_create_account);

        // Autocompletado para el textView de asignaturas
        ArrayAdapter asignaturasAutoCompleteAdapter = ArrayAdapter.createFromResource(this, R.array.asignaturas_array, android.R.layout.simple_spinner_dropdown_item);
        txSelectorAsignaturasDominadas.setAdapter(asignaturasAutoCompleteAdapter);

        btAddAsignatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(String.valueOf(txSelectorAsignaturasDominadas.getText()).trim().toLowerCase(Locale.ROOT) != "null" &&
                        !String.valueOf(txSelectorAsignaturasDominadas.getText()).isEmpty() &&
                        String.valueOf(txSelectorAsignaturasDominadas.getText()).trim() != ""){
                    asignaturasDominadasSeleccionadas.add(txSelectorAsignaturasDominadas.getText().toString());
                    txSelectorAsignaturasDominadas.setText("");
                    for (String a : asignaturasDominadasSeleccionadas) {
                        Log.i(TAG, a);
                    }

                }
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
            alumno.asignaturasDominadas = new ArrayList<>();

            Authentication.getInstance().signUp(alumno, new GenericCallback<String>() {
                @Override
                public void callback(String msg) {
                    if(msg.equals(GenericCallback.SUCCESS_CODE)){
                        redirectToHomeView();
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean validateFields() {
        boolean isValid = true;

        String email = txEmail.getText().toString();
        String password = txPassword.getText().toString();
        String passwordRepeated = txRepeatPassword.getText().toString();
        String uo = txUo.getText().toString();

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
        }

        if (!FormValidator.isNotEmpty(email)) {
            txEmail.setError(getText(R.string.email_empty));
            isValid = false;
        }

        if (!FormValidator.isNotEmpty(password)) {
            txPassword.setError(getText(R.string.password_empty));
            isValid = false;
        }

        if (!FormValidator.passwordMatched(password, passwordRepeated)) {
            Toast.makeText(getApplicationContext(), getText(R.string.password_not_matching), Toast.LENGTH_SHORT);
            isValid = false;
        }


//        if (!FormValidator.isPasswordValid(txPassword.getText().toString())) {
//            txPassword.setError(getText(R.string.password_invalid));
//            isValid = false;
//        }

        return isValid;
    }

    public void redirectToHomeView() {
        Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}