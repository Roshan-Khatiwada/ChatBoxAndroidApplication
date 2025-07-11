package com.example.chatbox;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.FirebaseUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
// import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
// import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

public class ChatsListActivity extends AppCompatActivity {
    private UserModel currentUserModel;
    private String username, halfUsername, myPhoneNumber;

    ImageButton search, threedot, TTT; // TTT=Tic Tac Toe
    BottomNavigationView bottom_nav;
    TextView appname;

    Profile profile;
    Chats chats;
    Reels reels; // Initialize the Reels fragment

    Dialog three_dot_dialog;
    Dialog loading2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_list);

        search = findViewById(R.id.search_btn);
        bottom_nav = findViewById(R.id.bottom_nav);
        threedot = findViewById(R.id.threedot);
        appname = findViewById(R.id.app_name);

        loading2=new Dialog(this);
        loading2.setContentView(R.layout.loading2);
        loading2.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        loading2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.roundcorners));

        loading2.setCancelable(false);


        profile = new Profile();
        chats = new Chats();
        reels = new Reels(); // Initialize the Reels fragment

        halfUsername = getIntent().getStringExtra("halfUsername");

        threedot.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatsListActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.three_dot_dialog, null);

            ImageButton quiz = dialogView.findViewById(R.id.quiz);
            ImageButton TTT = dialogView.findViewById(R.id.tictactoe);

            builder.setView(dialogView);
            three_dot_dialog = builder.create();
            three_dot_dialog.show();
            quiz.setOnClickListener(view1 -> {
                Intent intent = new Intent(ChatsListActivity.this, MainActivity.class);
                startActivity(intent);
            });
            TTT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatsListActivity.this,TicTacToe.class);
                    startActivity(intent);
                    finish();
                }
            });

        });

        search.setOnClickListener(v -> startActivity(new Intent(ChatsListActivity.this, Search.class)));

        bottom_nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.chats) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, chats).commit();
                appname.setText("Chats");
                search.setVisibility(View.VISIBLE);
                threedot.setVisibility(View.VISIBLE);

            }
            if (item.getItemId() == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, profile).commit();
                appname.setText("Profile");
                search.setVisibility(View.GONE);

            }
            if (item.getItemId() == R.id.reels) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, reels).commit();
                appname.setText("Reels");
                search.setVisibility(View.GONE);
                threedot.setVisibility(View.VISIBLE);


            }
            return true;
        });

        bottom_nav.setSelectedItemId(R.id.chats);

        getUserUsername();
        getFCMTOken();


    }

    private void getFCMTOken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtils.currentUserDetails().update("FCMToken", token);
            }
        });
    }

    private void getUserUsername() {
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                if (currentUserModel != null) {
                    username = currentUserModel.getUsername().trim();
                    myPhoneNumber = currentUserModel.getPhone().trim();
                    Toast.makeText(this, myPhoneNumber, Toast.LENGTH_SHORT).show();
                    startServices(myPhoneNumber);
                }
            } else {
                Log.e("ChatsListActivity", "Error getting user details", task.getException());
            }
        });
    }

    void startServices(String myPhoneNumber) {
        long appID = 1571840437;
        String appSign = "d1af395cf8a606ca5d21b36a1d5984bf1881f4e8123a9af87cf8792f376feb05";
        String userID = myPhoneNumber;
        String userName = myPhoneNumber;

    //     ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

    //     ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName, callInvitationConfig);
    // 
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}
