package com.example.chatbox;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {


    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.imageView5); // Replace with the actual ID of your ImageView

        // Use the slower scale-up animation for the TextView
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);


        // Use ValueAnimator for scaling both TextView and ImageView
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.1f);
        scaleAnimator.setDuration(5000); // 5 seconds
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();

                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            }
        });
        scaleAnimator.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(5000); // Wait for 5 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Splash_Screen.this, login_phoneNumber.class);
                startActivity(intent);
                Splash_Screen.this.finish();
            }
        }.start();
    }
}
