package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;

/**
 * SplashScreen: This activity represents the splash screen of the application.
 * It initializes the app and navigates to the appropriate screen based on the user's authentication status
 * or notification click action.
 */
public class Splash_Screen extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Check if there are any extras passed to the activity
        if (getIntent().getExtras() != null) {
            // If there are extras, check if the activity was launched from a notification click
            String userId = getIntent().getExtras().getString("userId");

            if (userId != null) {
                // If userId is not null, fetch user data from Firestore and navigate to message interface
                FirebaseUtils.allUserCollectionReference().document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                UserModel model = task.getResult().toObject(UserModel.class);

                                // Start the main activity and then the message interface
                                Intent mainIntent = new Intent(Splash_Screen.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainIntent);

                                Intent intent = new Intent(Splash_Screen.this, Message_interface.class);
                                AndroidUtils.passUserModelAsIntent(intent, model);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
            } else {
                // Handle the case where userId is null
                navigateToNextActivity();
            }
        } else {
            // If no extras, just navigate to the next activity
            navigateToNextActivity();
        }
    }

    /**
     * navigateToNextActivity: Helper method to navigate to the next activity after a delay.
     * This method checks if the user is logged in and navigates to the appropriate screen accordingly.
     */
    private void navigateToNextActivity() {
        new Handler().postDelayed(() -> {
            if (FirebaseUtils.isLoggedIn()) {
                // If user is logged in, navigate to ChatsListActivity
                startActivity(new Intent(Splash_Screen.this, ChatsListActivity.class));
            } else {
                // If user is not logged in, navigate to login_phoneNumber activity
                startActivity(new Intent(Splash_Screen.this, login_phoneNumber.class));
            }
            finish(); // Terminate the Splash Screen activity
        }, 1000); // Delay for 1000ms (1 second)
    }
}
