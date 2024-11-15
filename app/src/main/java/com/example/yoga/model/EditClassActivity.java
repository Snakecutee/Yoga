package com.example.yoga.model;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga.Classes.Class; // Ensure you have a Class model
import com.example.yoga.DatabaseHelper; // Ensure DatabaseHelper is set up
import com.example.yoga.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditClassActivity extends AppCompatActivity {
    private EditText teacherEditText, dateEditText, commentsEditText;
    private Button updateButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private DatabaseHelper dbHelper;
    private int classId;
    private int courseId;
    private ImageButton imageButtonBack;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        teacherEditText = findViewById(R.id.editTextTeacher);
        dateEditText = findViewById(R.id.editTextDate);
        commentsEditText = findViewById(R.id.editTextComments);
        updateButton = findViewById(R.id.buttonUpdate);
        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        classId = getIntent().getIntExtra("CLASS_ID", -1);
        courseId = getIntent().getIntExtra("course_id",-1);
        imageButtonBack = findViewById(R.id.imageButtonBack);


        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadClassDetails();
        dateEditText.setOnClickListener(v -> openDatePickerDialog());
        updateButton.setOnClickListener(v -> updateClass());
        imageButtonBack.setOnClickListener(v -> finish());
    }

    private void loadClassDetails() {
        Class classs = dbHelper.getClassById(classId,courseId);
        if (classs != null) {
            teacherEditText.setText(classs.getTeacherName());
            dateEditText.setText(classs.getDay());
            commentsEditText.setText(classs.getComments());

        }
    }

    private void openDatePickerDialog() {
        new DatePickerDialog(EditClassActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    dateEditText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateClass() {
        String teacher = teacherEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String comments = commentsEditText.getText().toString().trim();

        if (teacher.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Teacher and Date are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        updateClassInLocalDatabase(teacher, date, comments);
    }

    public void updateClassInLocalDatabase(String teacher, String date, String comments) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("teacher_name", teacher);
        values.put("class_day", date);
        values.put("comments", comments);

        int rowsAffected = db.update("class", values, "class_Id = ?", new String[]{String.valueOf(classId)});
        db.close();

        if (rowsAffected > 0) {
            Log.d("SQLiteUpdate", "Class updated in SQLite with ID: " + classId);
            // Cập nhật vào Firebase
            updateClassInFirebase(teacher, date, comments);
        } else {
            Log.e("SQLiteUpdate", "Failed to update class in SQLite.");
        }
    }


    private void updateClassInFirebase(String teacher, String date, String comments) {
        DatabaseReference firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("classes");
        Class updatedClass = new Class(classId, courseId, teacher, date, comments);
        firebaseDbRef.child(String.valueOf(classId)).setValue(updatedClass)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSync", "Class updated in Firebase with ID: " + classId);
                    Toast.makeText(EditClassActivity.this, "Class updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseSync", "Failed to update class in Firebase: " + e.getMessage());
                });
    }
}