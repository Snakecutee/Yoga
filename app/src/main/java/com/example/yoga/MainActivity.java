package com.example.yoga;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.yoga.index.HomeActivity;
import com.example.yoga.model.AddCourseActivity;
import com.example.yoga.model.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import android.view.MenuItem;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // Inflate MainActivity layout into BaseActivity content frame
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame));
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the selected listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.navigation_add) {
                    startActivity(new Intent(MainActivity.this, AddCourseActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.navigation_search) {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    return true;
                }
                return false;
            }
        });


        // Optional: Load the HomeActivity by default when MainActivity starts
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

}