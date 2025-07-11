package com.example.chatbox.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatbox.Model.MessageModel;
import com.example.chatbox.R;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;
import com.example.chatbox.login_OTP;
import com.example.chatbox.login_phoneNumber;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatsRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, ChatsRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private boolean count_for_date_forLeft=true;
    private boolean count_for_date_forRight=true;

    public ChatsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MessageModel model) {
        boolean isImageMessage = model.getImageUrl() != null && !model.getImageUrl().isEmpty();
        holder.messageimage.setVisibility(View.VISIBLE);
        FirebaseUtils.getOtherProfilePicStorageRef(model.getSenderId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.messageimage);

                    }
                });


        if (model.getSenderId().equals(FirebaseUtils.currentUserId())) {
            holder.leftImageview.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.messageimage.setVisibility(View.GONE);

            if (isImageMessage) {
                holder.leftImageview.setVisibility(View.GONE);//
                holder.RightImageview.setVisibility(View.VISIBLE);
                Glide.with(context).load(model.getImageUrl()).into(holder.RightImageview);
                holder.rightChatTextview.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.leftChatTextview.setVisibility(View.GONE);

            } else {
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.messageimage.setVisibility(View.GONE);
                holder.rightChatTextview.setText(model.getMessage());

                //
                holder.leftChatTextview.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.leftImageview.setVisibility(View.GONE);
                holder.RightImageview.setVisibility(View.GONE);
            }

            holder.receiverTime.setVisibility(View.GONE);
            holder.senderTime.setText(FirebaseUtils.timestampToString(model.getTimestamp()));
        } else {
            holder.RightImageview.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.messageimage.setVisibility(View.VISIBLE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);

            if (isImageMessage) {
                holder.RightImageview.setVisibility(View.GONE);
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.GONE);
                holder.leftImageview.setVisibility(View.VISIBLE);
                //
                Glide.with(context).load(model.getImageUrl()).into(holder.leftImageview);
                holder.leftChatTextview.setVisibility(View.GONE);
                holder.leftChatLayout.setVisibility(View.GONE);
                holder.messageimage.setVisibility(View.VISIBLE);
                holder.receiverTime.setVisibility(View.GONE);

            } else {
                holder.messageimage.setVisibility(View.VISIBLE);
                holder.leftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.messageimage.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setText(model.getMessage());
                //
                holder.rightChatLayout.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.GONE);
                holder.RightImageview.setVisibility(View.GONE);
                holder.leftImageview.setVisibility(View.GONE);

            }

            holder.senderTime.setVisibility(View.GONE);
            holder.receiverTime.setText(FirebaseUtils.timestampToString(model.getTimestamp()));
        }
        holder.receiverTime.setVisibility(View.INVISIBLE);
        holder.senderTime.setVisibility(View.INVISIBLE);

        holder.leftChatLayout.setOnClickListener(view -> {
            if (count_for_date_forLeft) {
                holder.receiverTime.setVisibility(View.VISIBLE);
                count_for_date_forLeft=false;
            }else {
                holder.receiverTime.setVisibility(View.INVISIBLE);
                count_for_date_forLeft= true;
            }
        });
        holder.rightChatLayout.setOnClickListener(view -> {
            if (count_for_date_forRight) {
                holder.senderTime.setVisibility(View.VISIBLE);
                count_for_date_forRight=false;
            }else {
                holder.senderTime.setVisibility(View.INVISIBLE);
                count_for_date_forRight= true;
            }
        });


        holder.leftChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (model.getSenderId().equals(FirebaseUtils.currentUserId())) {
                    deleteMessage(position);
                }
                return true;
            }
        });

        holder.rightChatLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (model.getSenderId().equals(FirebaseUtils.currentUserId())) {
                    deleteMessage(position);
                }
                return true;
            }
        });
    }

    private void deleteMessage(int position) {
        if (position >= 0 && position < getSnapshots().size()) {
            getSnapshots().getSnapshot(position).getReference().delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Invalid position for deletion", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    static class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview, receiverTime, senderTime;
        ImageView leftImageview, RightImageview;
        ImageView messageimage;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            senderTime = itemView.findViewById(R.id.senderTime);
            leftImageview = itemView.findViewById(R.id.left_image);
            RightImageview = itemView.findViewById(R.id.right_image);
            messageimage = itemView.findViewById(R.id.receiver_chat_img);

        }
    }
}
