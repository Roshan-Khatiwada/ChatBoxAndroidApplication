package com.example.chatbox;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Category extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
   private List<CategoryModel> list;

   private Dialog loadingdialog;
   Button back;

    private RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined in activity_category.xml.
        setContentView(R.layout.activity_category);
        // Toolbar setup for the activity.
        Toolbar toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);

        // Set the title of the toolbar to "Categories".
        getSupportActionBar().setTitle("");

        // Enable the Up button in the toolbar for navigation.
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Category.this,ChatsListActivity.class);
                startActivity(intent);
            }
        });

        // Find the RecyclerView in the layout and assign it to the 'recyclerView' variable.
        recyclerView = findViewById(R.id.rv);
        loadingdialog=new Dialog(this);
        loadingdialog.setContentView(R.layout.loading);
        loadingdialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            loadingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.roundcorners));

        loadingdialog.setCancelable(false);
        // Create a LinearLayoutManager for the RecyclerView to arrange items in a vertical orientation.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(recyclerView.VERTICAL);

        // Set the LinearLayoutManager to the RecyclerView.
        recyclerView.setLayoutManager(layoutManager);

        // Create a list of CategoryModel objects representing different categories.
        List<CategoryModel> list = new ArrayList<>();


        // Create a CategoryAdapter with the list of categories and set it to the RecyclerView.
        CategoryAdapter adapter = new CategoryAdapter(list);
        recyclerView.setAdapter(adapter);
        loadingdialog.show();
        myRef.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    // Use snapshot1 to get the value of each child
                    CategoryModel category = snapshot1.getValue(CategoryModel.class);
                    list.add(category);
                }
                adapter.notifyDataSetChanged();
                loadingdialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Category.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingdialog.dismiss();
                finish();
            }
        });



    }

    // onOptionsItemSelected is called when an item in the options menu is selected.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check if the selected item is the home/up button in the toolbar.
        if (item.getItemId() == android.R.id.home) {
            // Finish the activity when the home/up button is pressed.
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
