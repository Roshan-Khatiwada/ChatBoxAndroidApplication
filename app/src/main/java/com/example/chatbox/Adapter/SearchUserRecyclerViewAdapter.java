package com.example.chatbox.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.Model.UserModel;
import com.example.chatbox.R;
import com.example.chatbox.Utils.AndroidUtils;
import com.example.chatbox.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Firebase;

import org.w3c.dom.Text;

public class SearchUserRecyclerViewAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerViewAdapter.UserViewHolder> {
    Context context;

    public SearchUserRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel model) {
        holder.userNameTextView.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(FirebaseUtils.currentUserId())){
            holder.userNameTextView.setText(model.getUsername()+" (Me)");
        }

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserViewHolder(view);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView phoneText;
        ImageView profilePic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneText = itemView.findViewById(R.id.phone_text);
            userNameTextView = itemView.findViewById(R.id.user_name_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);

        }
    }
}
