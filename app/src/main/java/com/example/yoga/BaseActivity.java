package com.example.yoga;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga.model.AddCourseActivity;
import com.example.yoga.model.SearchActivity;

public class BaseActivity extends AppCompatActivity {

    protected Button addCourseButton, homeButton, searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);  // Load common layout

        // Initialize buttons from the layout
        addCourseButton = findViewById(R.id.addCourseButton);
        homeButton = findViewById(R.id.homeButton);
        searchButton = findViewById(R.id.searchButton);

        // Set up navigation
        setupNavigation();
    }

    private void setupNavigation() {
        // Navigate to AddCourseActivity when Add button is clicked
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to HomeActivity (assuming MainActivity is the home screen)
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to SearchActivity
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }
}
