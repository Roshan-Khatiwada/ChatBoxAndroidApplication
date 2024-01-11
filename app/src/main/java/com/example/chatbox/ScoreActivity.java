package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    private TextView scored;
    private Button done;
    private TextView total;
    private TextView celebration;
    private int scoredmarks, totalmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scored = findViewById(R.id.scored);
        done = findViewById(R.id.done);
        total = findViewById(R.id.total);
        celebration = findViewById(R.id.celebration);

        scored.setText(String.valueOf(getIntent().getIntExtra("score", 0)));
        total.setText("Out of " + String.valueOf(getIntent().getIntExtra("total", 0)));

        scoredmarks = getIntent().getIntExtra("score", 0);
        totalmarks = getIntent().getIntExtra("total", 0);

        if (scoredmarks == totalmarks) {
            celebration.setText("Perfect score! 🎉🎉");
        } else if (scoredmarks >= totalmarks * 0.9) {
            celebration.setText(" You're almost there! 👏");
        } else if (scoredmarks >= totalmarks * 0.7) {
            celebration.setText("Great effort! Keep it up! 👍");
        } else {
            celebration.setText("Good try!  💪");
        }

        // Add animation to the celebration text
        fadeInAnimation(celebration);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, Category.class);
                startActivity(intent);
            }
        });
    }

    private void fadeInAnimation(View view) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);

        // Scale animation to make the text grow from small to normal size
        Animation scaleUp = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(1000);

        // Translate animation to move the text upwards
        Animation translateUp = new TranslateAnimation(0, 0, 100, 0);
        translateUp.setDuration(1000);

        // Combine animations using AnimationSet
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(scaleUp);
        animationSet.addAnimation(translateUp);

        view.startAnimation(animationSet);
    }
}
