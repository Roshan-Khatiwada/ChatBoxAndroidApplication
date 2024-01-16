package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.FirebaseUtils;
import com.google.firebase.Timestamp;

public class login_userName extends AppCompatActivity {

    private String phnNumber;
    private Button Next;
    private EditText Username;
    UserModel userModel=new UserModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        Username = findViewById(R.id.username);
        Next = findViewById(R.id.Username_btn);

        Intent intent = getIntent();
        if (intent != null) {
            phnNumber = intent.getStringExtra("phoneNumber");
        }

        Next.setOnClickListener(v -> setUsername());
    }

    private void setUsername() {
        String username = Username.getText().toString().trim();
        if(username.isEmpty() || username.length()<3){
            Username.setError("Username length should be at least 3 chars");
            return;
        }

        if(userModel!=null){
            userModel.setUsername(username);
            userModel.setPhone(phnNumber);
            userModel.setCreatedTimestamp(Timestamp.now());
            userModel.setUserId(FirebaseUtils.currentUserId());
        }else{
            userModel = new UserModel(phnNumber,username,Timestamp.now(),FirebaseUtils.currentUserId());
        }

        FirebaseUtils.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Intent intent = new Intent(login_userName.this,ChatsListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

    }

    void getUsername(){
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                userModel =    task.getResult().toObject(UserModel.class);
                if(userModel!=null){
                    Username.setText(userModel.getUsername());
                }
            }
        });
    }

}
