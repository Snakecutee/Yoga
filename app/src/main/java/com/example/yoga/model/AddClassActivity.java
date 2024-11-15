package com.example.yoga.model;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton; // Thay đổi từ Button sang ImageButton
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga.Classes.Class; // Đảm bảo bạn đã có mô hình Class
import com.example.yoga.DatabaseHelper; // Đảm bảo DatabaseHelper đã được thiết lập
import com.example.yoga.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {
    private EditText teacherEditText, dateEditText, commentsEditText;
    private Button addButton; // Giữ nguyên nút thêm lớp
    private ImageButton imageButtonBack; // Thay đổi thành ImageButton
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private DatabaseHelper dbHelper; // Tham chiếu đến DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        teacherEditText = findViewById(R.id.editTextTeacher);
        dateEditText = findViewById(R.id.editTextDate);
        commentsEditText = findViewById(R.id.editTextComments);
        addButton = findViewById(R.id.buttonAdd);
        imageButtonBack = findViewById(R.id.imageButtonBack); // Khởi tạo ImageButton

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateEditText.setOnClickListener(v -> openDatePickerDialog());
        addButton.setOnClickListener(v -> AddClass());

        // Xử lý ImageButton trở về
        imageButtonBack.setOnClickListener(v -> finish()); // Quay lại trang trước

        // Lấy course_id từ Intent
        int courseId = getIntent().getIntExtra("course_id", -1);
        Log.d("AddClassActivity", "Received course_id: " + courseId);

        if (courseId == -1) {
            Toast.makeText(this, "Invalid course ID", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity nếu course ID không hợp lệ
        }
    }

    private void openDatePickerDialog() {
        new DatePickerDialog(AddClassActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    dateEditText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void AddClass() {
        String teacher = teacherEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String comments = commentsEditText.getText().toString().trim();

        // Lấy course_id từ Intent khi bấm nút Add Class
        int courseId = getIntent().getIntExtra("course_id", -1);

        if (teacher.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Teacher and Date are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi hàm lưu lớp học
        addClassToLocalDatabase(teacher, date, comments, courseId);
    }

    // Thêm lớp học vào SQLite
    public void addClassToLocalDatabase(String teacher, String date, String comments, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_id", courseId);
        values.put("teacher_name", teacher);
        values.put("class_day", date);
        values.put("comments", comments);

        // Chèn lớp học vào SQLite
        long result = db.insert("class", null, values);
        db.close();

        if (result != -1) {
            Log.d("SQLiteInsert", "Class added to SQLite with ID: " + result);
            // Đồng bộ với Firebase
            addClassToFirebase(teacher, date, comments, (int) result, courseId);
        } else {
            Log.e("SQLiteInsert", "Failed to add class to SQLite.");
        }
    }

    // Thêm lớp học vào Firebase
    private void addClassToFirebase(String teacher, String date, String comments, int classId, int courseId) {
        DatabaseReference firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("classes");

        // Tạo đối tượng lớp học
        Class newClass = new Class(classId, courseId, teacher, date, comments);
        firebaseDbRef.child(String.valueOf(classId)).setValue(newClass)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSync", "Class added to Firebase with ID: " + classId);
                    // Quay lại trang danh sách
                    finish(); // Kết thúc Activity hiện tại
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseSync", "Failed to add class to Firebase: " + e.getMessage());
                    Toast.makeText(this, "Failed to add class to Firebase.", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        teacherEditText.setText("");
        dateEditText.setText("");
        commentsEditText.setText("");
    }
}