package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import auth.Authentication;
import util.FormValidator;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText txEmail;
    private EditText txCompleteName;
    private EditText txUo;
    private EditText txPassword;
    private EditText txRepeatPassword;

    private Button btCreateAccount;
    private Button btRedirectToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setTitle("Crear una cuenta");

        initFields();

        btCreateAccount.setOnClickListener(new View.OnClickListener() {
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
        txPassword = (EditText) findViewById(R.id.text_password_create_account);
        txRepeatPassword = (EditText) findViewById(R.id.text_repeat_password_create_account);
        btCreateAccount = (Button) findViewById(R.id.button_signup_create_account);
        btRedirectToLogin = (Button) findViewById(R.id.button_login_create_account);
    }

    /**
     * Redirecciona a la vista de Inicio de sesión.
     */
    private void redirectToLogin(){
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void signUp() {
        if (validateFields()) {
            Authentication.getInstance().signUp(txEmail.getText().toString(), txPassword.getText().toString());
        }
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

//        if (!FormValidator.isPasswordValid(txPassword.getText().toString())) {
//            txPassword.setError(getText(R.string.password_invalid));
//            isValid = false;
//        }

        return isValid;
    }
}