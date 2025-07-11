package com.example.chatbox;


import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TicTacToe extends AppCompatActivity {
    private TextView textView4;
    private Button reversebutton;
    private boolean one=true;
    private boolean two=false;
    private int clickcount=0;

    private int count = 0;
    private boolean draw = false;
    private Button restartButton;
    private boolean result = true;
    private TextView textView;
    private TextView textView2;
    private ImageButton back;
    private int oldBackgroundColor;
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private char[][] buttons = {
            {'1', '1', '1'},
            {'1', '1', '1'},
            {'1', '1', '1'}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamestart); // Replace with the actual audio file in raw
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        // Find all buttons by their IDs
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        restartButton = findViewById(R.id.button10);
        reversebutton=findViewById(R.id.button11);
        back  = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicTacToe.this, ChatsListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Set OnClickListener for each button
        setButtonClickListener(button1, 0, 0);
        setButtonClickListener(button2, 0, 1);
        setButtonClickListener(button3, 0, 2);
        setButtonClickListener(button4, 1, 0);
        setButtonClickListener(button5, 1, 1);
        setButtonClickListener(button6, 1, 2);
        setButtonClickListener(button7, 2, 0);
        setButtonClickListener(button8, 2, 1);
        setButtonClickListener(button9, 2, 2);
        textView4 = findViewById(R.id.textView4);



        reversebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                clickcount++;
                if(clickcount%2==0)
                {
                    one=true;
                }
                else {
                    one=false;
                }
            }


        });
        // Set OnClickListener for the restart button
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gamestart); // Replace with the actual audio file in raw
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });


                ((Button) v).setTextColor(Color.parseColor("#FFCCCCCC"));

                // Use a handler to revert the changes after 1 second (1000 milliseconds)
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Button) v).setTextColor(Color.parseColor("#FFFFFFFF"));
                    }
                }, 100);
            }
        });

    }

    private void restartGame() {
        count = 0;
        result = true;

        // Reset text and text color for each button
        for (Button button : new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9}) {
            button.setText("");
            button.setTextColor(Color.WHITE);
        }

        // Reset the state of the game board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = '1';
            }
        }

        // Reset TextViews
        textView.setText("");
        textView2.setText("");

        // Enable buttons
        for (Button button : new Button[]{button1, button2, button3, button4, button5, button6, button7, button8, button9}) {
            button.setEnabled(true);
        }
    }

    private void setButtonClickListener(final Button button, final int a, final int b) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((Button) v).getText().toString().equals("") && result && one) {

                    if (count == 0 || count % 2 == 0) {
                        button.setText("O");
                        buttons[a][b] = 'O';
                        textView2.setText("");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    } else if (count % 2 == 1) {
                        button.setText("X");
                        buttons[a][b] = 'X';
                        textView2.setText("");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                    count++;

                    if (count % 2 == 1 && valid()) {
                        textView.setText(  "O wins!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameover); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });

                    } else if (count % 2 == 0 && valid()) {
                        textView.setText("X wins!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameover); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                    if (count == 9 && !valid()) {
                        textView.setText("Game Draw!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameover); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });

                    }
                }
                else if (((Button) v).getText().toString().equals("") && result && !one) {
                    if (count == 0 || count % 2 == 0) {
                        button.setText("X");
                        buttons[a][b] = 'X';
                        textView2.setText("");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    } else if (count % 2 == 1) {
                        button.setText("O");
                        buttons[a][b] = 'O';
                        textView2.setText("");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                    count++;

                    if (count % 2 == 1 && valid()) {
                        textView.setText("X wins!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameover); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });

                    } else if (count % 2 == 0 && valid()) {
                        textView.setText("O wins!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameover); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                    if (count == 9 && !valid()) {
                        textView.setText("Game Draw!");
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gameover); // Replace with the actual audio file in raw
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });

                    }
                } else {
                    if(valid()||count==9)
                    {
                        textView2.setText("Restart to play again!");
                    }
                    else {
                        textView2.setText("Invalid Place!");
                    }

                }
            }
        });
    }

    private boolean valid() {
        // Check for winning conditions
        // (O conditions)
        if ((buttons[0][0] == 'O' && buttons[0][1] == 'O' && buttons[0][2] == 'O') ||
                (buttons[0][0] == 'O' && buttons[1][0] == 'O' && buttons[2][0] == 'O') ||
                (buttons[0][1] == 'O' && buttons[1][1] == 'O' && buttons[2][1] == 'O') ||
                (buttons[0][2] == 'O' && buttons[1][2] == 'O' && buttons[2][2] == 'O') ||
                (buttons[1][0] == 'O' && buttons[1][1] == 'O' && buttons[1][2] == 'O') ||
                (buttons[2][0] == 'O' && buttons[2][1] == 'O' && buttons[2][2] == 'O') ||
                (buttons[0][0] == 'O' && buttons[1][1] == 'O' && buttons[2][2] == 'O') ||
                (buttons[0][2] == 'O' && buttons[1][1] == 'O' && buttons[2][0] == 'O')) {

            // Highlight the winning buttons in green
            if (buttons[0][0] == 'O' && buttons[0][1] == 'O' && buttons[0][2] == 'O') {
                button1.setTextColor(Color.GREEN);
                button2.setTextColor(Color.GREEN);
                button3.setTextColor(Color.GREEN);
            } else if (buttons[0][0] == 'O' && buttons[1][0] == 'O' && buttons[2][0] == 'O') {
                button1.setTextColor(Color.GREEN);
                button4.setTextColor(Color.GREEN);
                button7.setTextColor(Color.GREEN);
            } else if (buttons[0][1] == 'O' && buttons[1][1] == 'O' && buttons[2][1] == 'O') {
                button2.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button8.setTextColor(Color.GREEN);
            } else if (buttons[0][2] == 'O' && buttons[1][2] == 'O' && buttons[2][2] == 'O') {
                button3.setTextColor(Color.GREEN);
                button6.setTextColor(Color.GREEN);
                button9.setTextColor(Color.GREEN);
            } else if (buttons[1][0] == 'O' && buttons[1][1] == 'O' && buttons[1][2] == 'O') {
                button4.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button6.setTextColor(Color.GREEN);
            } else if (buttons[2][0] == 'O' && buttons[2][1] == 'O' && buttons[2][2] == 'O') {
                button7.setTextColor(Color.GREEN);
                button8.setTextColor(Color.GREEN);
                button9.setTextColor(Color.GREEN);
            } else if (buttons[0][0] == 'O' && buttons[1][1] == 'O' && buttons[2][2] == 'O') {
                button1.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button9.setTextColor(Color.GREEN);
            } else if (buttons[0][2] == 'O' && buttons[1][1] == 'O' && buttons[2][0] == 'O') {
                button3.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button7.setTextColor(Color.GREEN);
            }

            result = false;
            return true;
        }

        // (X conditions)
        if ((buttons[0][0] == 'X' && buttons[0][1] == 'X' && buttons[0][2] == 'X') ||
                (buttons[0][0] == 'X' && buttons[1][0] == 'X' && buttons[2][0] == 'X') ||
                (buttons[0][1] == 'X' && buttons[1][1] == 'X' && buttons[2][1] == 'X') ||
                (buttons[0][2] == 'X' && buttons[1][2] == 'X' && buttons[2][2] == 'X') ||
                (buttons[1][0] == 'X' && buttons[1][1] == 'X' && buttons[1][2] == 'X') ||
                (buttons[2][0] == 'X' && buttons[2][1] == 'X' && buttons[2][2] == 'X') ||
                (buttons[0][0] == 'X' && buttons[1][1] == 'X' && buttons[2][2] == 'X') ||
                (buttons[0][2] == 'X' && buttons[1][1] == 'X' && buttons[2][0] == 'X')) {

            // Highlight the winning buttons in green
            if (buttons[0][0] == 'X' && buttons[0][1] == 'X' && buttons[0][2] == 'X') {
                button1.setTextColor(Color.GREEN);
                button2.setTextColor(Color.GREEN);
                button3.setTextColor(Color.GREEN);
            } else if (buttons[0][0] == 'X' && buttons[1][0] == 'X' && buttons[2][0] == 'X') {
                button1.setTextColor(Color.GREEN);
                button4.setTextColor(Color.GREEN);
                button7.setTextColor(Color.GREEN);
            } else if (buttons[0][1] == 'X' && buttons[1][1] == 'X' && buttons[2][1] == 'X') {
                button2.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button8.setTextColor(Color.GREEN);
            } else if (buttons[0][2] == 'X' && buttons[1][2] == 'X' && buttons[2][2] == 'X') {
                button3.setTextColor(Color.GREEN);
                button6.setTextColor(Color.GREEN);
                button9.setTextColor(Color.GREEN);
            } else if (buttons[1][0] == 'X' && buttons[1][1] == 'X' && buttons[1][2] == 'X') {
                button4.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button6.setTextColor(Color.GREEN);
            } else if (buttons[2][0] == 'X' && buttons[2][1] == 'X' && buttons[2][2] == 'X') {
                button7.setTextColor(Color.GREEN);
                button8.setTextColor(Color.GREEN);
                button9.setTextColor(Color.GREEN);
            } else if (buttons[0][0] == 'X' && buttons[1][1] == 'X' && buttons[2][2] == 'X') {
                button1.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button9.setTextColor(Color.GREEN);
            } else if (buttons[0][2] == 'X' && buttons[1][1] == 'X' && buttons[2][0] == 'X') {
                button3.setTextColor(Color.GREEN);
                button5.setTextColor(Color.GREEN);
                button7.setTextColor(Color.GREEN);
            }

            result = false;
            return true;
        }

        return false;
    }
}
