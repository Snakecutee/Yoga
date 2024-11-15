package com.example.yoga.index;
import android.os.Bundle;

import android.widget.ListView;


import com.example.yoga.Adapter.CourseListAdapter;
import com.example.yoga.BaseActivity;
import com.example.yoga.Classes.Course;
import com.example.yoga.DatabaseHelper;
import com.example.yoga.R;

import java.util.ArrayList;
public class HomeActivity extends BaseActivity {
    private DatabaseHelper db;
    private CourseListAdapter adapter;
    private ListView courseListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.content_frame));
        db = new DatabaseHelper(this);
        courseListView = findViewById(R.id.courseListView);
        ArrayList<Course> courses = db.getAllCourses();

        // Initialize the adapter and set it to the ListView
        adapter = new CourseListAdapter(this, courses);
        courseListView.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Course> courses = db.getAllCourses();
        adapter.clear();
        adapter.addAll(courses);
        adapter.notifyDataSetChanged();
    }
}
