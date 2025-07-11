package com.example.chatbox.Adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.Model.VideoModel;
import com.example.chatbox.R;
import com.example.chatbox.Reels_upload;
import com.example.chatbox.Search;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class VideoHolder extends FirebaseRecyclerAdapter<VideoModel, VideoHolder.myviewholder> {
    DatabaseReference likereference;
    Boolean testclick=false;

    public VideoHolder(@NonNull FirebaseRecyclerOptions<VideoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull VideoModel model) {
        // Check if the adapter position is valid
        if (position != RecyclerView.NO_POSITION) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userid = firebaseUser.getUid();
            String postKey = getRef(position).getKey();

            likereference =  FirebaseDatabase.getInstance().getReference("likes");
                    holder.like_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            testclick = true;
                            likereference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (testclick == true){
                                                    if (snapshot.child(postKey).hasChild(userid)){
                                                    likereference.child(postKey).removeValue();
                                                    testclick = false;
                                                    }
                                                    else {
                                                            likereference.child(postKey).child(userid).setValue(true);
                                                            testclick = false;
                                                    }
                                                }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
            holder.getlikebuttonsStatus(postKey, userid);


            holder.setData(model);

        } else {
            Log.e("AdapterPosition", "Invalid position: " + position);
        }
    }




    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_video, parent, false);
        return new myviewholder(view);

    }

    class myviewholder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView title;
        Button desc;
        ProgressBar pbar;
        ImageView like_btn;
        TextView like_text;
        MediaController mediaController;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.textVideoTitle);
            desc = itemView.findViewById(R.id.textVideoDescription);
            pbar = itemView.findViewById(R.id.videoProgressBar);
            like_btn = itemView.findViewById(R.id.like_btn);
            like_text = itemView.findViewById(R.id.like_text);
            mediaController = new MediaController(desc.getContext());
            videoView.setMediaController(mediaController);


            desc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username = desc.getText().toString();
                    Intent intent = new Intent(itemView.getContext(), Search.class);
                    intent.putExtra("USERNAME_EXTRA_KEY", username);
                    itemView.getContext().startActivity(intent);

                }
            });
        }
        public void getlikebuttonsStatus(String postKey, String userid) {
            likereference = FirebaseDatabase.getInstance().getReference("likes");
            likereference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(postKey).hasChild(userid)){
                        int likecount = (int)snapshot.child(postKey).getChildrenCount();
                        like_text.setText(String.valueOf(likecount));

                        like_btn.setImageResource(R.drawable.filled_heart);


                    }
                    else {
                        int likecount = (int)snapshot.child(postKey).getChildrenCount();
                        like_text.setText(String.valueOf(likecount));

                        like_btn.setImageResource(R.drawable.heart_svgrepo_com);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        void setData(VideoModel obj) {
            // Use Uri.parse for video path
            videoView.setVideoPath(obj.getVurl());
            title.setText(obj.getTitle());
            desc.setText(obj.getUsername());

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    pbar.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // Handle the error
                    Log.e("VideoPlayer", "Error during playback. What: " + what + ", Extra: " + extra);
                    return false;
                }
            });
        }
    }
}
