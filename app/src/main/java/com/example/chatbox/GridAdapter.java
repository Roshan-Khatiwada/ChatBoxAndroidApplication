package com.example.chatbox;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

// Adapter for displaying grid items representing sets
public class GridAdapter extends BaseAdapter {

    private int sets = 0; // Number of sets
    private String category; // Category of the sets

    // Constructor to set the number of sets and the category
    public GridAdapter(int sets, String category) {
        this.sets = sets;
        this.category = category;
    }

    // Get the total number of items in the adapter
    @Override
    public int getCount() {
        return sets;
    }

    // Get the data item associated with the specified position in the data set.
    @Override
    public Object getItem(int i) {
        return null;
    }

    // Get the row id associated with the specified position in the list.
    @Override
    public long getItemId(int i) {
        return 0;
    }

    // Get a View that displays the data at the specified position in the data set.
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setitem, viewGroup, false);
        } else {
            view = convertView;
        }

        // Set an OnClickListener to handle item clicks
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Question activity with category and set number as extras
                Intent intent = new Intent(viewGroup.getContext(), Question.class);
                intent.putExtra("category", category);
                intent.putExtra("setNo", i + 1);
                viewGroup.getContext().startActivity(intent);
            }
        });

        // Set the text of the TextView to display the set number
        ((TextView) view.findViewById(R.id.text)).setText(String.valueOf(i + 1));

        return view;
    }
}
