package com.example.chatbox;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

    private int sets=0;
    private String category;
    // Constructor to set the number of sets
    public GridAdapter(int sets, String category) {
        this.sets = sets;
        this.category=category;
    }

    @Override
    public int getCount() {
        return sets;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setitem, viewGroup, false);
        } else {
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(viewGroup.getContext(),Question.class);
                intent.putExtra("category",category);
                intent.putExtra("setNo",i+1);
                viewGroup.getContext().startActivity(intent);
            }
        });
        ((TextView) view.findViewById(R.id.text)).setText(String.valueOf(i + 1));
        return view;
    }
}
