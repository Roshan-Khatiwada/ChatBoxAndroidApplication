package com.example.chatbox.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.Message_interface;
import com.example.chatbox.Model.MessageModel;
import com.example.chatbox.R;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Firebase;

import org.w3c.dom.Text;

public class ChatsRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, ChatsRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    public ChatsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull MessageModel model) {
        if(model.getSenderId().equals(FirebaseUtils.currentUserId())){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsgTextView.setText(model.getMessage());
        }else{
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftMsgTextView.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    public static class ChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout,rightLayout;
        TextView leftMsgTextView,rightMsgTextView;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_message_layout);
            rightLayout = itemView.findViewById(R.id.right_message_layout);
            leftMsgTextView = itemView.findViewById(R.id.left_message_textview);
            rightMsgTextView = itemView.findViewById(R.id.right_message_textview);
        }
    }
}
