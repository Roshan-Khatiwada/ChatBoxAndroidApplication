package com.example.chatbox;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.Adapter.ChatsRecyclerAdapter;
import com.example.chatbox.Model.ChatroomModel;
import com.example.chatbox.Model.MessageModel;
import com.example.chatbox.Model.UserModel;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Message_interface extends AppCompatActivity {
    String chatRoomId;
    ChatroomModel chatroomModel;

    ImageButton backBtn;
    ImageView profilePic;
    TextView username;
    ImageButton audioCallBtn;
    ImageButton videoCallBtn;
    ImageButton threeDotBtn;
    RecyclerView msgRecyclerView;
    EditText typeMsgEditText;
    ImageButton sendMsgBtn;
    TextView active;
    UserModel usermodel;
    ChatsRecyclerAdapter chatsRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_interface);

        usermodel = new UserModel();
        usermodel.setUsername(getIntent().getStringExtra("username"));
        usermodel.setPhone(getIntent().getStringExtra("phone"));
        usermodel.setUserId(getIntent().getStringExtra("userId"));


//        chatRoomId = FirebaseUtils.currentUserId()+"_"+usermodel.getUserId();

        String currentUserId = FirebaseUtils.currentUserId();
        String otherUserId = usermodel.getUserId();
        if (currentUserId.compareTo(otherUserId) > 0) {
            String temp = currentUserId;
            currentUserId = otherUserId;
            otherUserId = temp;

        }
        chatRoomId = currentUserId + "_" + otherUserId;

        backBtn = findViewById(R.id.back_btn);
        username = findViewById(R.id.username);
        msgRecyclerView = findViewById(R.id.chatRecyclerView);
        typeMsgEditText = findViewById(R.id.sendMsgEdittext);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        active =findViewById(R.id.active);
        profilePic = findViewById(R.id.Profile_pic);

        FirebaseUtils.getOtherProfilePicStorageRef(usermodel.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtils.setProfilePic(this,uri,profilePic);
                    }
                });

        backBtn.setOnClickListener(v -> onBackPressed());

        username.setText(usermodel.getUsername());

        sendMsgBtn.setOnClickListener(v->{

            String message = typeMsgEditText.getText().toString().trim();
            if (!message.isEmpty()){
                if (chatroomModel != null) {
                    sendMsgToUser(message);
                } else {
                    // Handle the case where chatroomModel is null, perhaps by notifying the user or retrying
                    Toast.makeText(Message_interface.this, "Chat room not initialized. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getOrCreateChatroom();
        setupChatRecyclerView();
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtils.getChatroomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query,MessageModel.class).build();

        chatsRecyclerAdapter = new ChatsRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        msgRecyclerView.setLayoutManager(manager);
        msgRecyclerView.setAdapter(chatsRecyclerAdapter);
        chatsRecyclerAdapter.startListening();
        chatsRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                msgRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMsgToUser(String message) {
        if (chatroomModel != null) {
            chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessage(message);
            FirebaseUtils.getChatRoomReference(chatRoomId).set(chatroomModel);

            MessageModel messageModel = new MessageModel(message, FirebaseUtils.currentUserId(), Timestamp.now());

            FirebaseUtils.getChatroomMessageReference(chatRoomId).add(messageModel).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    typeMsgEditText.setText("");
                } else {
                    // Handle the case where message sending fails, if needed
                    Toast.makeText(Message_interface.this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getOrCreateChatroom() {
        FirebaseUtils.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    chatroomModel = new ChatroomModel(
                            chatRoomId,
                            Arrays.asList(FirebaseUtils.currentUserId(), usermodel.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtils.getChatRoomReference(chatRoomId).set(chatroomModel);
                }

            } else {
                // Handle the case where fetching the chatroom fails, if needed
                Toast.makeText(Message_interface.this, "Failed to fetch chat room. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
