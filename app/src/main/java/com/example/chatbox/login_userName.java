package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.FirebaseUtils;
import com.google.firebase.Timestamp;

// Activity for setting up user's username during registration
public class login_userName extends AppCompatActivity {

    private String phnNumber;
    private Button Next;
    private EditText Username;
    UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        Username = findViewById(R.id.username);
        Next = findViewById(R.id.Username_btn);

        // Get phone number passed from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            phnNumber = intent.getStringExtra("phoneNumber");
        }

        // Set onClickListener for the "Next" button to proceed to set username
        Next.setOnClickListener(v -> setUsername());
    }

    // Method to set the username in Firebase and navigate to the ChatsListActivity
    private void setUsername() {
        String username = Username.getText().toString().trim();

        // Validate username length
        if (username.isEmpty() || username.length() < 3) {
            Username.setError("Username length should be at least 3 characters");
            return;
        }

        // Create UserModel object with the provided username, phone number, and other details
        if (userModel != null) {
            userModel.setUsername(username);
            userModel.setPhone(phnNumber);
            userModel.setCreatedTimestamp(Timestamp.now());
            userModel.setUserId(FirebaseUtils.currentUserId());
        } else {
            userModel = new UserModel(phnNumber, username, Timestamp.now(), FirebaseUtils.currentUserId());
        }

        // Set the user details in Firebase Firestore
        FirebaseUtils.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If successful, navigate to the ChatsListActivity
                Intent intent = new Intent(login_userName.this, ChatsListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

    }

    // Method to fetch the username from Firebase Firestore
    void getUsername() {
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    // Set the fetched username in the EditText field
                    Username.setText(userModel.getUsername());
                }
            }
        });
    }

}
