package com.example.chatbox;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class Question extends AppCompatActivity {
    public static final String FILE_NAME="QUIZPULSE";
    public static final String KEY_NAME="QUESTIONs";
    private TextView question, num;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private LinearLayout options;
    private Button back, front;
    private int count = 0;
    private List<QuestionModel> list;
    private int poition = 0;

    private int score = 0;
    private String category;
    private int setNo;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Dialog loadingdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        question = findViewById(R.id.question);
        num = findViewById(R.id.num);
        options = findViewById(R.id.options);
        front = findViewById(R.id.front);
        list = new ArrayList<>();
        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 1);
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor=preferences.edit();

        loadingdialog=new Dialog(this);
        loadingdialog.setContentView(R.layout.loading);
        loadingdialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.roundcorners));
        loadingdialog.setCancelable(false);
        loadingdialog.show();
        myRef.child("Sets").child(category).child("questions").orderByChild("setNo").equalTo(setNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear(); // Clear the list before populating it
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            list.add(snapshot1.getValue(QuestionModel.class));
                        }
                        if (list.size() > 0) {
                            // Move your UI-related code here
                            for (int i = 0; i < 4; i++) {
                                options.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkanswer((Button) v);
                                    }
                                });
                            }


                            Playanim(question, 0, list.get(poition).getQuestion());

                            front.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    front.setEnabled(false);
                                    front.setAlpha(0.7f);
                                    poition++;
                                    enableoption(true);
                                    if (poition == list.size()) {
                                        Intent intent=new Intent(Question.this,ScoreActivity.class);
                                        intent.putExtra("score",score);
                                        intent.putExtra("total",list.size());
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    count = 0;
                                    Playanim(question, 0, list.get(poition).getQuestion());
                                }
                            });
                        } else {
                            finish();
                            Toast.makeText(Question.this, "No Questions Available In This Set", Toast.LENGTH_LONG   ).show();
                        }
                        loadingdialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Question.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingdialog.dismiss();
                        finish();
                    }
                });
    }


    private void Playanim(View view, int value, String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {
                        if (value == 0 && count < 4) {
                            String option = "";
                            if (count == 0) {
                                option = list.get(poition).getOptionA();
                            } else if (count == 1) {
                                option = list.get(poition).getOptionB();
                            } else if (count == 2) {
                                option = list.get(poition).getOptionC();
                            } else if (count == 3) {
                                option = list.get(poition).getOptionD();
                            }
                            Playanim(options.getChildAt(count), 0, option);
                            count++;
                        }
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        if (value == 0) {
                            try {
                                ((TextView) view).setText(data);
                                num.setText(poition + 1 + "/" + list.size());
                            } catch (ClassCastException ex) {
                                ((Button) view).setText(data);
                            }
                            view.setTag(data);
                            Playanim(view, 1, data);
                        }
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {
                    }
                });
    }


    private void checkanswer(Button selectedoption) {
        enableoption(false);
        front.setEnabled(true);
        front.setAlpha(1);

        String correctAnswerTag = list.get(poition).getCorrectANS();
        Button correctOption = (Button) options.findViewWithTag(correctAnswerTag);

        if (correctOption != null) {
            if (selectedoption.getText().toString().equals(correctAnswerTag)) {
                // Correct answer logic
                score++;
                selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF4CAF50")));
            } else {
                // Incorrect answer logic
                selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                correctOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF4CAF50")));
            }
        }
    }


    private void enableoption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            Button optionButton = (Button) options.getChildAt(i);
            optionButton.setEnabled(enable);

            if (enable) {

                    optionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));

            } else {
                optionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));
            }
        }
    }

}
