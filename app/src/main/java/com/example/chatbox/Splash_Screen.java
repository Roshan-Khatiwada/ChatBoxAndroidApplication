package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;

public class Splash_Screen extends AppCompatActivity {


    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtils.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this,MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, Message_interface.class);
                            AndroidUtils.passUserModelAsIntent(intent,model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });


        }else{
            new Handler().postDelayed(() -> {
                if(FirebaseUtils.isLoggedIn()){
                    startActivity(new Intent(Splash_Screen.this,ChatsListActivity.class));
                }else{
                    startActivity(new Intent(Splash_Screen.this,login_phoneNumber.class));
                }
                finish();
            },1000);
        }
    }
}
