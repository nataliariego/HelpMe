package chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.helpme.model.Chat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatService {

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public static final String TAG = "CHAT_SERVICE";

    public void sendMessage(){

    }

    public void receiveMessage(){

    }

    public void showAllChats(){
        db.getReference(Chat.REFERENCE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data :  snapshot.getChildren()){
                    String uid = data.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
