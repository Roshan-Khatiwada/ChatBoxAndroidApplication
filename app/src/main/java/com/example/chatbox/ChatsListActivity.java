package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ChatsListActivity extends AppCompatActivity {
    ImageButton search;
    BottomNavigationView bottom_nav ;

    Profile profile;
    Chats chats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_list);
        search = findViewById(R.id.search_btn);
        bottom_nav = findViewById(R.id.bottom_nav);

        profile = new Profile();
        chats = new Chats();

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