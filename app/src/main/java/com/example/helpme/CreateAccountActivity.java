package com.example.helpme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private Button btVerAsignaturasDominadasSeleccionadas;
    private Button btEliminarAsignaturasDominadas;
    private Button btCreateAccount;
    private Button btRedirectToLogin;
    private Button btAddAsignatura;

    private static final AlumnoController alumnoController = new AlumnoController();
    private final AsignaturaViewModel asignaturaViewModel = new AsignaturaViewModel();
    private final List<String> asignaturasDisponibles = new ArrayList<>();
    private ArrayAdapter asignaturasAutoCompleteAdapter;
    private final HashMap<String, Object> asignaturasDominadasSeleccionadas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setTitle(R.string.crear_cuenta);

        initFields();

        btCreateAccount.setOnClickListener(view -> signUp());

        btRedirectToLogin.setOnClickListener(view -> redirectToLogin());

    }

    @Override
    protected void onResume() {
        super.onResume();

        changeButtonColors(false);

        if (asignaturasDisponibles.size() == 0) {
            obtenerAsignaturasDisponibles();
        }
    }

    private void changeButtonColors(boolean habilitar) {
        boolean anyAsignaturasSeleccionadas = asignaturasDominadasSeleccionadas.size() > 0;


        btEliminarAsignaturasDominadas.setEnabled(anyAsignaturasSeleccionadas);

        if (anyAsignaturasSeleccionadas || habilitar) {
            btVerAsignaturasDominadasSeleccionadas.setEnabled(true);
            btEliminarAsignaturasDominadas.setEnabled(true);

            btVerAsignaturasDominadasSeleccionadas.setBackgroundColor(Color.rgb(255, 239, 211));

            btEliminarAsignaturasDominadas.setBackgroundColor(Color.rgb(219, 15, 0));
            btEliminarAsignaturasDominadas.setTextColor(Color.rgb(255, 255, 255));
        } else {
            btVerAsignaturasDominadasSeleccionadas.setEnabled(false);
            btEliminarAsignaturasDominadas.setEnabled(false);

            btVerAsignaturasDominadasSeleccionadas.setBackgroundColor(Color.rgb(209, 209, 209));

            btEliminarAsignaturasDominadas.setBackgroundColor(Color.rgb(209, 209, 209));
            btEliminarAsignaturasDominadas.setTextColor(Color.rgb(12, 12, 50));
        }
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
        btEliminarAsignaturasDominadas = (Button) findViewById(R.id.buttonEliminarAsignaturasDominadas);
        asignaturasAutoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, asignaturasDisponibles);
        txSelectorAsignaturasDominadas.setAdapter(asignaturasAutoCompleteAdapter);

        addListeners();
    }

    /**
     * Añade los eventos a los componentes del activity.
     */
    private void addListeners() {
        /* Evento click en boton añadir asignatura */
        btAddAsignatura.setOnClickListener(view -> {
            if (!String.valueOf(txSelectorAsignaturasDominadas.getText()).trim().toLowerCase(Locale.ROOT).equals("null") && !String.valueOf(txSelectorAsignaturasDominadas.getText()).isEmpty() && !String.valueOf(txSelectorAsignaturasDominadas.getText()).trim().equals("")) {

                String asigName = txSelectorAsignaturasDominadas.getText().toString();

                AsignaturaController.getInstance().findByName(asigName, payload -> {
                    int pos = asignaturasDominadasSeleccionadas.size() == 0 ? 1 : asignaturasDominadasSeleccionadas.size() - 1;
                    asignaturasDominadasSeleccionadas.put(String.valueOf(pos), payload);

                    Log.i(TAG, payload.toString());

                    txSelectorAsignaturasDominadas.setText("");
                    changeButtonColors(true);
                });
            }
        });

        /* Evento click para restablecer las asignaturas dominadas seleccionadas */
        btEliminarAsignaturasDominadas.setOnClickListener(view -> {
            asignaturasDominadasSeleccionadas.clear();
            changeButtonColors(false);
            Toast.makeText(CreateAccountActivity.this, R.string.asig_dom_borradas, Toast.LENGTH_SHORT).show();
        });

        /* Evento click en ver asignaturas seleccionadas */
        btVerAsignaturasDominadasSeleccionadas.setOnClickListener(view -> {
            AsignaturasDominadasBottomSheetDialogFragment fragment = AsignaturasDominadasBottomSheetDialogFragment.newInstance(asignaturasDominadasSeleccionadas);
            fragment.show(getSupportFragmentManager(), fragment.getTag());

        });
    }

    /**
     * Obtiene todas las asignaturas disponibles en la aplicación.
     */
    private void obtenerAsignaturasDisponibles() {
        asignaturasDisponibles.clear();
        asignaturaViewModel.getAllAsignaturas().observe(this, availableSubjects -> {
            if (availableSubjects != null) {
                availableSubjects.forEach(a -> {
                    if (a != null) {

                        AsignaturaDto asig = new AsignaturaDto();
                        asig.id = a.getId();
                        asig.nombre = a.getNombre();

                        asignaturasDisponibles.add(asig.nombre);
                    }
                });

                asignaturasAutoCompleteAdapter.notifyDataSetChanged();
                Log.d(TAG, "Asignaturas: " + asignaturasDisponibles);
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

    /**
     * Crea una nueva cuenta de usuario en la aplicación con los siguientes datos:
     * <ul>
     *     <li>nombre del alumno</li>
     *     <li>Identificador UO</li>
     *     <li>Correo electrónico corporativo</li>
     *     <li>contraseña. No se almacena en la base de datos.</li>
     *     <li>imagen de perfil</li>
     *     <li>Asignaturas dominadas por el alumno. (Opcional)</li>
     * </ul>
     */
    private void signUp() {
        if (validateFields()) {
            AlumnoDto alumno = new AlumnoDto();
            alumno.nombre = txCompleteName.getText().toString();
            alumno.uo = txUo.getText().toString();
            alumno.email = txEmail.getText().toString();
            alumno.password = Objects.requireNonNull(txPassword.getText()).toString();
            alumno.urlFoto = "https://ui-avatars.com/api/?name=" + alumno.nombre;
            alumno.asignaturasDominadas = asignaturasDominadasSeleccionadas;

            Authentication.getInstance().signUp(alumno, (GenericCallback<String>) msg -> {
                if (msg.equals(GenericCallback.SUCCESS_CODE)) {
                    redirectToHomeView();
                    Log.i(TAG, "SUCCESS");
                } else {
                    Log.i(TAG, "ERROR");
                }
            });
        }
    }

    /**
     * Validacion de los campos del formulario del activity de crear una cuenta.
     *
     * @return true si validan todos los campos y false en caso contrario.
     */
    private boolean validateFields() {

        String email = txEmail.getText().toString();
        String password = Objects.requireNonNull(txPassword.getText()).toString();
        String passwordRepeated = Objects.requireNonNull(txRepeatPassword.getText()).toString();
        String uo = txUo.getText().toString();

        if (!FormValidator.isNotEmpty(uo)) {
            txUo.setError(getText(R.string.uo_empty));
            return false;
        }

        alumnoController.findByUO(uo, alumno -> {
            if (alumno != null) {
                Toast.makeText(getApplicationContext(), getText(R.string.uo_already_exists), Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), getText(R.string.password_not_matching), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Redirecciona a la vista de inicio.
     */
    public void redirectToHomeView() {
        Intent intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}