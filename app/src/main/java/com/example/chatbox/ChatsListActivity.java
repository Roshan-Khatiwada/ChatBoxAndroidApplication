package com.example.chatbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.FirebaseUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class ChatsListActivity extends AppCompatActivity {
    private UserModel currentUserModel;
    private String username ,halfUsername;
    ImageButton search, threedot , TTT; // TTT=Tic Tac Toe
    BottomNavigationView bottom_nav ;
    TextView appname;

    Profile profile;
    Chats chats;
    Dialog three_dot_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_list);
        search = findViewById(R.id.search_btn);
        bottom_nav = findViewById(R.id.bottom_nav);
        threedot=findViewById(R.id.threedot);
        appname = findViewById(R.id.app_name);

        profile = new Profile();
        chats = new Chats();
        halfUsername = getIntent().getStringExtra("halfUsername");

        threedot.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.three_dot_dialog, null);

            ImageButton quiz = dialogView.findViewById(R.id.quiz);
            ImageButton TTT = dialogView.findViewById(R.id.tictactoe);

            builder.setView(dialogView);
            three_dot_dialog = builder.create();
            three_dot_dialog.show();
            quiz.setOnClickListener(view1 -> {
               Intent intent = new Intent(ChatsListActivity.this, MainActivity.class);
               startActivity(intent);
            });
            // on clicking TTT image button
            TTT.setOnClickListener(view1 -> {
                if (username != null) {
                    String[] parts = username.split("\\s+");


                    if (parts.length > 0) {
                        halfUsername = parts[0];
                    } else {
                        halfUsername = username;
                    }

                    Intent intent = new Intent(ChatsListActivity.this, TicTacToe.class);
                    intent.putExtra("username", halfUsername);
                    startActivity(intent);
                } else {
                    // Handle the case where username is null
                    Log.e("ChatsListActivity", "Username is null");
                }
            });


        });


        search.setOnClickListener(v-> startActivity(new Intent(ChatsListActivity.this, Search.class)));

        bottom_nav.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.chats){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,chats).commit();
            }
            if(item.getItemId()==R.id.profile){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,profile).commit();
            }
            return true;
        });

        bottom_nav.setSelectedItemId(R.id.chats);

        getUserUsername();
        getFCMTOken();
    }

    private void getFCMTOken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
            String token = task.getResult();
               FirebaseUtils.currentUserDetails().update("FCMToken",token);
           }
        });
    }
    private void getUserUsername() {
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    username = currentUserModel.getUsername().toString().trim();
                }
            } else {
                Log.e("ChatsListActivity", "Error getting user details", task.getException());
            }
        });
    }
    @Override
    protected void onStop() {
        // Handle cleanup or resource release
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Handle cleanup or resource release
        super.onDestroy();
    }
}
