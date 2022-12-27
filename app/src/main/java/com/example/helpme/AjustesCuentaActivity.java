package com.example.helpme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import auth.Authentication;
import chat.AlumnoStatus;
import chat.ChatService;

public class AjustesCuentaActivity extends AppCompatActivity {

    private Button btLogout;
    private Button btResetPasswordLink;
    private Button btVerifyAccount;

    private LinearLayout layoutVerificacionEmailLabel;

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_cuenta);
        setTitle("Ajustes de la cuenta");

        initFields();
    }

    private void initFields() {
        btLogout = (Button) findViewById(R.id.button_logout);
        btResetPasswordLink = (Button) findViewById(R.id.button_reset_password);
        btVerifyAccount = (Button) findViewById(R.id.button_verificar_email);
        layoutVerificacionEmailLabel = (LinearLayout) findViewById(R.id.layout_correo_verificado_ajustes);

        /* Cerrar sesión */
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Cambiar estado a OFFLINE */
                ChatService.getInstance().changeCurrentUserStatus(AlumnoStatus.OFFLINE.toString().toLowerCase(), new ChatService.AlumnoStatusCallback() {
                    @Override
                    public void callback() {
                        Authentication.getInstance().signOut();
                        redirectToLogin();
                        finish();
                    }
                });
            }
        });

        /* Restablecer contraseña cuenta mediante link al correo*/
        btResetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authentication.getInstance().sendResetPasswordEmail(userInSession.getEmail());

                Toast.makeText(AjustesCuentaActivity.this, "Recibirás un correo para restablecer la contraseña. Revisa la bandeja de spam.", Toast.LENGTH_LONG).show();
            }
        });

        /* Verificar cuenta */
        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            layoutVerificacionEmailLabel.setVisibility(View.VISIBLE);
            btVerifyAccount.setVisibility(View.INVISIBLE);
        }

        btVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Authentication.getInstance().sendEmailVerification();

                Toast.makeText(AjustesCuentaActivity.this, "Recibirás un correo para verificar el correo de tu cuenta. Revisa la bandeja de Spam.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AjustesCuentaActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}