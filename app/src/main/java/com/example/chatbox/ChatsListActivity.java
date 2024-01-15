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

import com.example.chatbox.Utils.FirebaseUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class ChatsListActivity extends AppCompatActivity {
    ImageButton search, threedot;
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

        threedot.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.three_dot_dialog, null);

            ImageButton quiz = dialogView.findViewById(R.id.quiz);

            builder.setView(dialogView);
            three_dot_dialog = builder.create();
            three_dot_dialog.show();
            quiz.setOnClickListener(view1 -> {
               Intent intent = new Intent(ChatsListActivity.this, MainActivity.class);
               startActivity(intent);
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
}
