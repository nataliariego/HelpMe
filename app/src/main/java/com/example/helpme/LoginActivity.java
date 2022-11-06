package com.example.helpme;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import auth.Authentication;
import util.FormValidator;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText txEmail;
    private TextInputEditText txPassword;
    private Button btLogin;
    private Button btCreateAnAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Iniciar sesión");

        initFields();


        btCreateAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToCreateAnAccountView();
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    signIn();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().getCurrentUser();
    }

    private void signIn() {
        Authentication.getInstance().signIn(txEmail.getText().toString(), txPassword.getText().toString());
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (!FormValidator.isNotEmpty(txEmail.getText().toString())) {
            txEmail.setError(getText(R.string.email_empty));
            isValid = false;
        }

        if (!FormValidator.isNotEmpty(txPassword.getText().toString())) {
            txPassword.setError(getText(R.string.password_empty));
            isValid = false;
        }

        if (!FormValidator.isEmailValid(txEmail.getText().toString())) {
            txEmail.setError(getText(R.string.email_invalid));
            isValid = false;
        }

/*        if (!FormValidator.isPasswordValid(txPassword.getText().toString())) {
            txPassword.setError(getText(R.string.password_invalid));
            isValid = false;
        }*/

        return isValid;
    }

    /**
     * Redireccionamiento a la página de crear una cuenta.
     */
    private void redirectToCreateAnAccountView() {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);

        startActivity(intent);
    }

    /**
     * Inicialización de los campos del formulario de login.
     */
    private void initFields() {
        btLogin = (Button) findViewById(R.id.button_login_login);
        txEmail = (TextInputEditText) findViewById(R.id.text_email_login);
        txPassword = (TextInputEditText) findViewById(R.id.text_password_login);
        btCreateAnAccount = (Button) findViewById(R.id.button_create_account_login);
    }
}