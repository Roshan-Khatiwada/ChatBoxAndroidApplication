package com.example.chatbox.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.ChatsListActivity;
import com.example.chatbox.Message_interface;
import com.example.chatbox.Model.ChatroomModel;
import com.example.chatbox.Model.UserModel;
import com.example.chatbox.R;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatsAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatsAdapter.ChatroomModelViewHolder> {
    Context context;

    public RecentChatsAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtils.getOtheruserFormChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSender = model.getLastMessageSenderId().equals(FirebaseUtils.currentUserId());

                        UserModel otheruserModel = task.getResult().toObject(UserModel.class);

                        // Add a null check for otheruserModel
                        if (otheruserModel != null) {
                            FirebaseUtils.getOtherProfilePicStorageRef(otheruserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(t -> {
                                        if(t.isSuccessful()){
                                            Uri uri  = t.getResult();
                                            AndroidUtils.setProfilePic(context,uri,holder.profilePic);
                                        }
                                    });

                            holder.userNameTextView.setText(otheruserModel.getUsername());
                            if(lastMessageSender){
                                holder.recentText.setText("You : "+model.getLastMessage());
                            }
                            else{
                                holder.recentText.setText(model.getLastMessage());
                            }
                            holder.timeStamp.setText(FirebaseUtils.timestampToString(model.getLastMessageTimestamp()));

                            holder.itemView.setOnClickListener(view -> {
                                Intent intent = new Intent(context, Message_interface.class);
                                AndroidUtils.passUserModelAsIntent(intent,otheruserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        }
                    }
                });
    }


    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    public static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView recentText;
        ImageView profilePic;
        TextView timeStamp;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            recentText = itemView.findViewById(R.id.lastMSg_text);
            userNameTextView = itemView.findViewById(R.id.user_name_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            timeStamp = itemView.findViewById(R.id.timeStamp);

        }
    }
}
