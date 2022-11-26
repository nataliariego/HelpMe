package com.example.helpme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import chat.ChatService;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "CHAT_ACTIVITY";

    private EditText txMensajeAEnviar;
    private ImageButton btEnviarMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initFields();

        btEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Enviando mensaje...");
                ChatService.getInstance().sendMessage(txMensajeAEnviar.getText().toString());
            }
        });

    }

    /**
     * Inicializa los campos del activity.
     */
    private void initFields() {
        btEnviarMensaje = (ImageButton) findViewById(R.id.button_enviar_mensaje_chat);
        txMensajeAEnviar = (EditText) findViewById(R.id.text_mensaje_enviar_chat);
    }
}