package com.example.yoga.index;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga.Adapter.ClassListAdapter;
import com.example.yoga.Classes.Class;
import com.example.yoga.DatabaseHelper;
import com.example.yoga.R;
import com.example.yoga.model.AddClassActivity;

import java.util.List;

public class Classyoga extends AppCompatActivity {
    private Button buttonNewClass;
    private ImageButton imageButtonBack; // Thêm nút trở về
    private DatabaseHelper db;
    private ClassListAdapter adapter;
    private ListView classListView;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_list_item);

        buttonNewClass = findViewById(R.id.buttonNewClass);
        imageButtonBack = findViewById(R.id.imageButtonBack); // Khởi tạo nút trở về
        db = new DatabaseHelper(this);
        courseId = getIntent().getIntExtra("course_id", -1);

        buttonNewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Classyoga.this, AddClassActivity.class);
                intent.putExtra("course_id", courseId);
                startActivity(intent);
            }
        });


        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        classListView = findViewById(R.id.listViewClasses);
        loadClasses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }

    private void loadClasses() {
        List<Class> classyogas = db.getAllClasses(courseId);
        adapter = new ClassListAdapter(this, classyogas);
        classListView.setAdapter(adapter);
    }
}