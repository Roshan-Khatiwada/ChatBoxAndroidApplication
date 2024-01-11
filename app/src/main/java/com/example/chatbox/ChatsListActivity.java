package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ChatsListActivity extends AppCompatActivity {
    ImageButton search, threedot;
    BottomNavigationView bottom_nav ;

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

        profile = new Profile();
        chats = new Chats();

        threedot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.three_dot_dialog, null);

                // Corrected the button initialization
                ImageButton quiz = dialogView.findViewById(R.id.quiz);

                builder.setView(dialogView);
                three_dot_dialog = builder.create();
                three_dot_dialog.show();
                quiz.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent = new Intent(ChatsListActivity.this, MainActivity.class);
                       startActivity(intent);
                    }
                });

            }

        });
        search.setOnClickListener(v->{
            startActivity(new Intent(ChatsListActivity.this, Search.class));
        });

        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.chats){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,chats).commit();
                }
                if(item.getItemId()==R.id.profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,profile).commit();
                }
                return true;
            }
        });

        bottom_nav.setSelectedItemId(R.id.chats);
    }
}
