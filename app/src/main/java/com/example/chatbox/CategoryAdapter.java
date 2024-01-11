package com.example.chatbox;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

// CategoryAdapter is a custom RecyclerView Adapter responsible for adapting a list of CategoryModel objects
// for display in a RecyclerView.

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    // List of CategoryModel objects to be displayed in the RecyclerView.
    private List<CategoryModel> categoryModellist;

    // Constructor to initialize the CategoryAdapter with a list of CategoryModel.
    public CategoryAdapter(List<CategoryModel> list) {
        this.categoryModellist = list;
    }

    // onCreateViewHolder is called when RecyclerView needs a new ViewHolder to represent an item in the list.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the category item from category_item.xml.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

        // Return a new instance of ViewHolder with the inflated view.
        return new ViewHolder(view);
    }

    // onBindViewHolder is called to bind the data to a particular ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set data for the ViewHolder based on the CategoryModel at the current position.
        holder.setData(categoryModellist.get(position).getUrl(), categoryModellist.get(position).getName(),categoryModellist.get(position).getSets());
    }

    // getItemCount returns the total number of items in the data set.
    @Override
    public int getItemCount() {
        return categoryModellist.size();
    }

    // ViewHolder represents each item in the RecyclerView.
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView title;

        // Constructor for ViewHolder, initializes views.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.Title);
        }

        // setData method sets the data (image URL and title) for the views in the ViewHolder.
        private void setData(String url, final String title,final int sets) {
            // Use Glide library to load the image from the URL into the ImageView.
            Glide.with(itemView.getContext()).load(url).into(imageView);

            // Set the title text for the TextView.
            this.title.setText(title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(itemView.getContext(),SetsActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("sets",sets);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
