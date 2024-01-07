package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

public class Search extends AppCompatActivity {
    ImageButton back_btn;
    TextInputEditText username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        back_btn = findViewById(R.id.back_btn);
        username = findViewById(R.id.username_edittext);

        back_btn.setOnClickListener(v->{
            onBackPressed();
        });

    }
}