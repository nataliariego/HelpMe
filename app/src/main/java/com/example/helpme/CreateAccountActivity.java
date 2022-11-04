package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setTitle("Crear una cuenta");

        initFields();

        btCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void initFields() {
        txUo = (EditText) findViewById(R.id.text_uo_create_account);
        txEmail = (EditText) findViewById(R.id.text_email_create_account);
        txCompleteName = (EditText) findViewById(R.id.text_nombre_completo_create_account);
        txPassword = (EditText) findViewById(R.id.text_password_create_account);
        txRepeatPassword = (EditText) findViewById(R.id.text_repeat_password_create_account);
        btCreateAccount = (Button) findViewById(R.id.button_create_account);
    }

    private void login() {
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