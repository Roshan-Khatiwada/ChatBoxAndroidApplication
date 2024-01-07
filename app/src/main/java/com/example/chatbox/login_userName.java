package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class login_userName extends AppCompatActivity {

    private String phnNumber;
    private Button Next;
    private EditText Username;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("Registration");

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

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = Username.getText().toString().trim();

                db.collection("Registration")
                        .whereEqualTo("Phone Number", phnNumber)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        saveUsernameAndNumber(username, phnNumber);
                                    } else {
                                        Toast.makeText(login_userName.this, "error checking", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle error
                                    Toast.makeText(login_userName.this, "Error checking phone number", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void saveUsernameAndNumber(String username, String phnNumber) {
        DocumentReference userDocument = db.collection("Registration").document(username);

        final int totalchar=username.length();
        if(totalchar<6)
        {
            Toast.makeText(this, "At least 6 characters required", Toast.LENGTH_SHORT).show();
        }else {
            Map<String, Object> user = new HashMap<>();
            user.put("Username", username);
            user.put("Phone Number", phnNumber);

            userDocument.set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(login_userName.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(login_userName.this, ChatsListActivity.class);
                                intent.putExtra("phoneNumber", phnNumber);
                                intent.putExtra("Username",username);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(login_userName.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }



    }
}
