package com.example.helpme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import adapter.AlumnoAdapter;
import adapter.ChatAdapter;
import chat.ChatService;
import controller.AlumnoController;
import dto.ChatSummaryDto;

public class ListarChatsActivity extends AppCompatActivity {
    public static final String TAG = "LISTAR_CHATS_ACTIVITY";
    private static final String CHAT_SELECCIONADO = "chat_seleccionado";

    private DatabaseReference dbReference = FirebaseDatabase.getInstance(ChatService.DB_URL).getReference();
    private ChatAdapter chatAdapter;
    private AlumnoAdapter alumnoAdapter;

    private RecyclerView recyclerListadoChats;
    private List<ChatSummaryDto> chats;

    private FloatingActionButton fabNuevoChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_chats);
        setTitle("Chats");

        initFields();

    }

    private void initFields() {
        recyclerListadoChats = findViewById(R.id.recycler_listado_chats);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerListadoChats.setLayoutManager(layoutManager);

        fabNuevoChat = (FloatingActionButton) findViewById(R.id.fab_nuevo_chat);

        fabNuevoChat.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                AlumnoController.getInstance().findAll().observe(ListarChatsActivity.this, alumnos -> {

                    if (alumnos != null) {
                        for (int i = 0; i < alumnos.size(); i++) {
                            Log.d(TAG, "ALUMNO: " + alumnos.get(i).getNombre());
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarChats();

        chatAdapter = new ChatAdapter(chats, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ChatSummaryDto chat) {
                clickOnItem(chat);
            }
        });

        recyclerListadoChats.setAdapter(chatAdapter);

    }

    private void clickOnItem(ChatSummaryDto chat) {
        Intent intent = new Intent(ListarChatsActivity.this, ChatActivity.class);
        intent.putExtra(CHAT_SELECCIONADO, chat);

        startActivity(intent);
    }

    private void cargarChats() {
        dbReference.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d(TAG, "ds: " + ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}