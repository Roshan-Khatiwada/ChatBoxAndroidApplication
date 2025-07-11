package com.example.chatbox;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.chatbox.Adapter.RecentChatsAdapter;
import com.example.chatbox.Model.ChatroomModel;
import com.example.chatbox.Utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Chats extends Fragment {
    RecyclerView recyclerView;
    RecentChatsAdapter adapter;
    Dialog loading2;

    public Chats() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recentChatsRecyclerView);
        loading2 = new Dialog(requireContext());
        loading2.setContentView(R.layout.loading2);
        loading2.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loading2.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.roundcorners));
        loading2.setCancelable(false);
        loading2.show();

        // Setup RecyclerView
        Query query = FirebaseUtils.allChatroomCollectionsReference()
                .whereArrayContains("userIds", FirebaseUtils.currentUserId())
                .orderBy("lastMessageSenderId", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new RecentChatsAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Check if query is successful, dismiss loading dialog and start listening to adapter
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loading2.dismiss(); // Dismiss loading dialog
                adapter.startListening(); // Start listening to Firestore changes
            } else {
                // If query fails, handle the error and dismiss loading dialog after 2 seconds
                new Handler().postDelayed(() -> loading2.dismiss(), 2000);
                Log.e("FirestoreQuery", "Error getting documents: ", task.getException());
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening(); // Start listening to Firestore changes when fragment starts
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening(); // Stop listening to Firestore changes when fragment stops
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged(); // Notify adapter when fragment resumes to update any changes
    }
}
