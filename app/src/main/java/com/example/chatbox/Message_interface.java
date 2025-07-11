package com.example.chatbox;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
//import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
// import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
// import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Message_interface extends AppCompatActivity {

    // Declaring variables
    String anotheruserprofileUri;
    String chatRoomId;
    ChatroomModel chatroomModel;
    UserModel usermodel;
    ChatsRecyclerAdapter chatsRecyclerAdapter;

    ImageButton backBtn;
    ImageView profilePic;
    TextView username;
    RecyclerView msgRecyclerView;
    String halfUsername;
    EditText typeMsgEditText;
    ImageButton sendMsgBtn, sendImage, emojibtn;
    String anotherUserPhoneNumber;

    // ZegoSendCallInvitationButton audioCallBtn;
    // ZegoSendCallInvitationButton videoCallBtn;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_interface);

        // Extracting username from the intent
        String[] parts = Objects.requireNonNull(getIntent().getStringExtra("username")).split("\\s+");
        if (parts.length > 0) {
            halfUsername = parts[0];
        } else {
            halfUsername = getIntent().getStringExtra("username");
        }

        // Initializing UI components
//        audioCallBtn = findViewById(R.id.audio_call_btn);
//        videoCallBtn = findViewById(R.id.video_call_btn);
        emojibtn = findViewById(R.id.emoji);
        sendImage = findViewById(R.id.send_img);
        usermodel = new UserModel();
        typeMsgEditText = findViewById(R.id.sendMsgEdittext);
        relativeLayout = findViewById(R.id.relative);
        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.relative)).build(typeMsgEditText);
        usermodel.setUsername(halfUsername);
        usermodel.setPhone(getIntent().getStringExtra("phone"));
        usermodel.setUserId(getIntent().getStringExtra("userId"));
        anotherUserPhoneNumber = Objects.requireNonNull(getIntent().getStringExtra("phone")).trim();

        // Setting up voice and video call buttons
        startVoiceCall(anotherUserPhoneNumber);
//        audioCallBtn.setBackgroundResource(R.drawable.audio_call);
        startVideoCall(anotherUserPhoneNumber);
//        videoCallBtn.setBackgroundResource(R.drawable.video_call);

        // Generating chat room ID
        String currentUserId = FirebaseUtils.currentUserId();
        String otherUserId = usermodel.getUserId();
        if (currentUserId.compareTo(otherUserId) > 0) {
            String temp = currentUserId;
            currentUserId = otherUserId;
            otherUserId = temp;
        }
        chatRoomId = currentUserId + "_" + otherUserId;

        // Initializing UI components
        backBtn = findViewById(R.id.back_btn);
        username = findViewById(R.id.username);
        msgRecyclerView = findViewById(R.id.chatRecyclerView);
        typeMsgEditText = findViewById(R.id.sendMsgEdittext);
        sendMsgBtn = findViewById(R.id.sendMsgBtn);
        profilePic = findViewById(R.id.Profile_pic);

        // Retrieving user profile picture and setting it in ImageView
        FirebaseUtils.getOtherProfilePicStorageRef(usermodel.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        Uri uri = t.getResult();
                        AndroidUtils.setProfilePic(this, uri, profilePic);
                    }
                });

        // Toggle emoji keyboard
        emojibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        // Handle back button click
        backBtn.setOnClickListener(v -> onBackPressed());

        // Set username in TextView
        username.setText(usermodel.getUsername());

        // Send message button click listener
        sendMsgBtn.setOnClickListener(v -> {
            String message = typeMsgEditText.getText().toString().trim();

            if (!message.isEmpty()) {
                if (chatroomModel != null) {
                    sendMsgToUser(message);
                } else {
                    Toast.makeText(Message_interface.this, "Chat room not initialized. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Get or create chat room
        getOrCreateChatroom();

        // Setup chat RecyclerView
        setupChatRecyclerView();

        // Send image button click listener
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(Message_interface.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1200, 1200)
                        .start();

                EmojiTextView emojiTextView = (EmojiTextView) LayoutInflater.from(v.getContext()).inflate(R.layout.emoji, relativeLayout,false);
                emojiTextView.setText(typeMsgEditText.getText().toString());
                relativeLayout.addView(emojiTextView);
                typeMsgEditText.getText().clear();
            }
        });
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        uploadImageToStorage(uri);
    }

    // Setup chat RecyclerView
    private void setupChatRecyclerView() {
        Query query = FirebaseUtils.getChatroomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        chatsRecyclerAdapter = new ChatsRecyclerAdapter(options, getApplicationContext());
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

    // Send message to user
    private void sendMsgToUser(String message) {
        if (chatroomModel != null) {
            chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessage(message);
            FirebaseUtils.getChatRoomReference(chatRoomId).set(chatroomModel);

            MessageModel messageModel = new MessageModel(message, FirebaseUtils.currentUserId(), Timestamp.now(), "", "");

            FirebaseUtils.getChatroomMessageReference(chatRoomId).add(messageModel).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    typeMsgEditText.setText("");
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sentmsg); // Replace with the actual audio file in raw
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                } else {
                    Toast.makeText(Message_interface.this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Get or create chat room
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
                Toast.makeText(Message_interface.this, "Failed to fetch chat room. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Start voice call
    private void startVoiceCall(String targetUserID) {
//        audioCallBtn.setIsVideoCall(false);
        // audioCallBtn.setResourceID("zego_uikit_call");
        // audioCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserID)));
    }

    // Start video call
    private void startVideoCall(String targetUserID) {
//        videoCallBtn.setIsVideoCall(true);
        // videoCallBtn.setResourceID("zego_uikit_call");
        // videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID, targetUserID)));
    }

    // Upload image to Firebase storage
    private void uploadImageToStorage(Uri imageUri) {
        if (imageUri != null) {
            String imageName = "chatroom_images/" + chatRoomId + "/" + System.currentTimeMillis() + ".jpg";

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imageName);

            storageReference.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                sendImageMessage(imageUrl);
                            });
                        } else {
                            Toast.makeText(Message_interface.this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Send image message
    private void sendImageMessage(String imageUrl) {
        if (chatroomModel != null) {
            chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessage("sent a photo");

            FirebaseUtils.getChatRoomReference(chatRoomId).set(chatroomModel);

            if (!TextUtils.isEmpty(imageUrl)) {
                MessageModel messageModel = new MessageModel("", FirebaseUtils.currentUserId(), Timestamp.now(), "", imageUrl);

                FirebaseUtils.getChatroomMessageReference(chatRoomId).add(messageModel).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Optionally handle successful image sending
                    } else {
                        Toast.makeText(Message_interface.this, "Failed to send image. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
