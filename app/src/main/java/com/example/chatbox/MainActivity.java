package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;




// MainActivity is the entry point of the Quiz Pulse application.
// It extends AppCompatActivity, which is the base class for activities in Android.

public class MainActivity extends AppCompatActivity {

    // Button used to initiate the start of the quiz or navigation to categories.

    private Button start, back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined in activity_main.xml.
        setContentView(R.layout.activity_main);

        // Find the Button with the ID 'button' in the layout and assign it to the 'start' variable.
        start = findViewById(R.id.button);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the Button is clicked, create an Intent to navigate from MainActivity to Category activity.
                Intent intent = new Intent(MainActivity.this, ChatsListActivity.class);

                // Start the Category activity.
                startActivity(intent);
            }
        });

        // Set an OnClickListener on the 'start' Button.
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the Button is clicked, create an Intent to navigate from MainActivity to Category activity.
                Intent intent = new Intent(MainActivity.this, Category.class);

                // Start the Category activity.
                startActivity(intent);
            }
        });

    }
}
