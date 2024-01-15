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

import com.example.chatbox.Message_interface;
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

        FirebaseUtils.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri  = t.getResult();
                        AndroidUtils.setProfilePic(context,uri,holder.profilePic);
                    }
                });

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context , Message_interface.class);
            intent.putExtra("username",model.getUsername());
            intent.putExtra("phone",model.getPhone());
            intent.putExtra("userId",model.getUserId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

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
