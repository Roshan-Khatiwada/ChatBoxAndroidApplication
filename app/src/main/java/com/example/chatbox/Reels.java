package com.example.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chatbox.Adapter.VideoHolder;
import com.example.chatbox.Model.VideoModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class Reels extends Fragment {
    FloatingActionButton add;
    ViewPager2 viewPager;
    VideoHolder videoHolder;

    public Reels() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reels, container, false);

        // Add code to make the status bar fullscreen
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        add = view.findViewById(R.id.floating);
        viewPager = view.findViewById(R.id.viewpager);


        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Videos"), VideoModel.class)
                .build();

        videoHolder = new VideoHolder(options);
        viewPager.setAdapter(videoHolder);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Reels_upload.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        videoHolder.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        videoHolder.stopListening();
    }
}
