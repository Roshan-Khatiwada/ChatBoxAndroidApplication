package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class Splash_Screen extends AppCompatActivity {

private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        textView=findViewById(R.id.textView3);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);
        textView.setAnimation(anim);

        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(Splash_Screen.this,login_phoneNumber.class);
                startActivity(intent);
                Splash_Screen.this.finish();

            }
        }.start();
    }
}
