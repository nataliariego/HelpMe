package com.example.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import auth.Authentication;
import chat.AlumnoStatus;
import chat.ChatService;

public class AjustesCuentaActivity extends AppCompatActivity {

    private Button btLogout;
    private Button btResetPasswordLink;
    private Button btVerifyAccount;

    private final FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_cuenta);
        setTitle(R.string.account_settings);
        initFields();
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
        LinearLayout layoutVerificacionEmailLabel = (LinearLayout) findViewById(R.id.layout_correo_verificado_ajustes);

        addListeners();

        /* Verificar cuenta */
        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
            layoutVerificacionEmailLabel.setVisibility(View.VISIBLE);
            btVerifyAccount.setVisibility(View.GONE);
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

        /* Verificar el correo electrónico de la cuenta */
        btVerifyAccount.setOnClickListener(view -> {
            Authentication.getInstance().sendEmailVerification();
            Toast.makeText(AjustesCuentaActivity.this, R.string.revisa_contraseña, Toast.LENGTH_LONG).show();
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AjustesCuentaActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}