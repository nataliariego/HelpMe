package com.example.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import auth.Authentication;
import chat.AlumnoStatus;
import chat.ChatService;
import controller.AlumnoController;

public class AjustesCuentaActivity extends AppCompatActivity {

    private Button btLogout;
    private Button btResetPasswordLink;
    private Button btVerifyAccount;
    private Button btDeleteAccount;

    private final FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_cuenta);
        setTitle(R.string.account_settings);
        initFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(userInSession == null){
            redirectToLogin();
        }
    }

    /**
     * Inicializar los componentes del layout. Si el usuario ya tiene el email verificado
     * no se le mostrará el botón de verificación de cuenta.
     *
     * @see #addListeners()
     */
    private void initFields() {
        btLogout = (Button) findViewById(R.id.button_logout);
        btResetPasswordLink = (Button) findViewById(R.id.button_reset_password);
        btVerifyAccount = (Button) findViewById(R.id.button_verificar_email);
        btDeleteAccount = (Button) findViewById(R.id.button_eliminar_cuenta);

        LinearLayout layoutVerificacionEmailLabel = (LinearLayout) findViewById(R.id.layout_correo_verificado_ajustes);

        addListeners();

        /* Verificar cuenta */
        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
            btVerifyAccount.setVisibility(View.GONE);
            layoutVerificacionEmailLabel.setVisibility(View.VISIBLE);
        } else {
            layoutVerificacionEmailLabel.setVisibility(View.GONE);
            btVerifyAccount.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Añade los listeners necesarios para los elementos del activity.
     */
    private void addListeners() {
        /* Cerrar sesión */
        btLogout.setOnClickListener(view -> {
            /* Cambiar estado a OFFLINE */
            ChatService.getInstance().changeCurrentUserStatus(AlumnoStatus.OFFLINE.toString().toLowerCase(), () -> {
                Authentication.getInstance().signOut();
                redirectToLogin();
                finish();
            });
        });

        /* Restablecer contraseña cuenta mediante link al correo*/
        btResetPasswordLink.setOnClickListener(view -> {
            Authentication.getInstance().sendResetPasswordEmail(userInSession.getEmail());

            Toast.makeText(AjustesCuentaActivity.this, R.string.olvida_contraseña, Toast.LENGTH_LONG).show();
        });

        /* Eliminar la cuenta del alumno. Incluye todos los mensajes y datos personales de éste. */
        btDeleteAccount.setOnClickListener(view -> showDeleteAccountDialog());

        /* Verificar el correo electrónico de la cuenta */
        btVerifyAccount.setOnClickListener(view -> {
            Authentication.getInstance().sendEmailVerification();
            Toast.makeText(AjustesCuentaActivity.this, R.string.revisa_contraseña, Toast.LENGTH_LONG).show();
        });
    }

    private void showDeleteAccountDialog() {
        // Mostrar mensaje de confirmación
        AlertDialog.Builder deleteConfirmationDialog = new AlertDialog.Builder(AjustesCuentaActivity.this);
        deleteConfirmationDialog.setTitle("Eliminar cuenta");
        deleteConfirmationDialog.setMessage("Al confirmar, se borrarán todos tus datos de la aplicación salvo las dudas publicadas y chats que tengas abiertos con otros alumnos ¿Estás seguro?")
                .setCancelable(true)
                .setPositiveButton("Sí, borra la cuenta", (dialogInterface, i) -> deleteAccount())
                .setNegativeButton("No, he cambiado de idea", (dialogInterface, i) -> Toast.makeText(this, "Acción cancelada", Toast.LENGTH_SHORT).show())
                .setIcon(R.drawable.ic_baseline_remove_circle_outline_24)
                .show();
    }

    private void deleteAccount() {
        Authentication.getInstance().deleteAccount(new AlumnoController.DeleteAlumnoCallback() {
            @Override
            public void onSuccess(final AlumnoController.DeleteAlumnoPayload payload) {
                Toast.makeText(AjustesCuentaActivity.this, "Cuenta eliminada correctamente", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AjustesCuentaActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AjustesCuentaActivity.this, "Se ha producido un error al eliminar la cuenta", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AjustesCuentaActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}