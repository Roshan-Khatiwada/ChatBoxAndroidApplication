package com.example.chatbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.Model.MessageModel;
import com.example.chatbox.R;
import com.example.chatbox.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatsRecyclerAdapter extends FirestoreRecyclerAdapter<MessageModel, ChatsRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    public ChatsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_row, parent, false);
        ChatModelViewHolder viewHolder = new ChatModelViewHolder(view);

        // Set initial layout parameters
        RelativeLayout.LayoutParams layoutParamsLeft = (RelativeLayout.LayoutParams) viewHolder.leftLayout.getLayoutParams();
        layoutParamsLeft.addRule(RelativeLayout.ALIGN_PARENT_START);
        viewHolder.leftLayout.setLayoutParams(layoutParamsLeft);

        RelativeLayout.LayoutParams layoutParamsRight = (RelativeLayout.LayoutParams) viewHolder.rightLayout.getLayoutParams();
        layoutParamsRight.addRule(RelativeLayout.ALIGN_PARENT_END);
        viewHolder.rightLayout.setLayoutParams(layoutParamsRight);

        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull MessageModel model) {
        if (model.getSenderId().equals(FirebaseUtils.currentUserId())) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsgTextView.setText(model.getMessage());
        } else {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftMsgTextView.setText(model.getMessage());
        }
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
