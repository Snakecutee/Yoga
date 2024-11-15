package com.example.yoga.model;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.yoga.BaseActivity;
import com.example.yoga.Classes.Course;
import com.example.yoga.DatabaseHelper;
import com.example.yoga.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditCourseActivity extends BaseActivity {
    private EditText dayOfWeek, timeOfCourse, capacity, duration, price, description;
    private RadioGroup classTypeGroup;
    private Button updateButton;
    private DatabaseReference firebaseDbRef;
    private int courseId;
    private RadioButton flow, aerial, family;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        // Khởi tạo Firebase Database Reference
        firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("courses");

        dayOfWeek = findViewById(R.id.editTextDayOfWeek);
        timeOfCourse = findViewById(R.id.editTextTimeOfCourse);
        capacity = findViewById(R.id.editTextCapacity);
        duration = findViewById(R.id.editTextDuration);
        price = findViewById(R.id.editTextPrice);
        description = findViewById(R.id.editTextDescription);
        classTypeGroup = findViewById(R.id.editclassTypeGroup);
        updateButton = findViewById(R.id.EditupdateButton);
        flow = findViewById(R.id.flow_yoga);
        aerial = findViewById(R.id.aerial_yoga);
        family = findViewById(R.id.family_yoga);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        courseId = intent.getIntExtra("Id", -1);
        if (courseId != -1) {
            loadCourseData(intent);
        }

        // Thiết lập sự kiện cho nút cập nhật
        updateButton.setOnClickListener(v -> updateCourse());
    }

    private void loadCourseData(Intent intent) {
//        Log.d("EditCourse", "Loading course data for ID: " + courseId);
        dayOfWeek.setText(intent.getStringExtra("day"));
        timeOfCourse.setText(intent.getStringExtra("time"));
        capacity.setText(intent.getStringExtra("capacity"));
        duration.setText(intent.getStringExtra("duration"));
        price.setText(intent.getStringExtra("price"));
        description.setText(intent.getStringExtra("description"));

        String classType = intent.getStringExtra("classType");
        Log.d("EditCourse", "Class Type: " + classType);
        if (classType != null) {
            switch (classType) {
                case "Flow Yoga":
                    flow.setChecked(true);
                    break;
                case "Aerial Yoga":
                    aerial.setChecked(true);
                    break;
                case "Family Yoga":
                    family.setChecked(true);
                    break;
                default:
                    Log.d("EditCourse", "Unknown classType: " + classType);
            }
        } else {
            Log.d("EditCourse", "classType is null");
        }
    }

    private void updateCourse() {
        String day = dayOfWeek.getText().toString().trim();
        String time = timeOfCourse.getText().toString().trim();
        String courseCapacity = capacity.getText().toString().trim();
        String courseDuration = duration.getText().toString().trim();
        String coursePrice = price.getText().toString().trim();
        String courseDescription = description.getText().toString().trim();
        String classType = getSelectedClassType();

        if (!isInputValid(day, time, courseCapacity, courseDuration, coursePrice, classType)) {
            return;
        }

        Course updatedCourse = new Course(courseId, day, time, courseCapacity, courseDuration, coursePrice, classType, courseDescription);

        DatabaseHelper db = new DatabaseHelper(this);
        if (db.updateCourse(updatedCourse)) {
            syncToFirebase(updatedCourse);
            Toast.makeText(this, "Course updated successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update course in SQLite.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedClassType() {
        int selectedClassTypeId = classTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedClassTypeButton = findViewById(selectedClassTypeId);
        return selectedClassTypeButton != null ? selectedClassTypeButton.getText().toString() : "";
    }

    private boolean isInputValid(String day, String time, String capacity, String duration, String price, String classType) {
        if (day.isEmpty() || !isValidDay(day)) {
            Toast.makeText(this, "Please enter a valid day of the week.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (time.isEmpty() || !time.matches("^(0[1-9]|1[0-9]|2[0-4]):([0-5][0-9])$")) {
            Toast.makeText(this, "Please enter a valid time (e.g., 10:00).", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (capacity.isEmpty() || duration.isEmpty() || price.isEmpty() || classType.isEmpty()) {
            Toast.makeText(this, "All required fields must be filled.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isPositiveInteger(capacity, "Capacity") || !isPositiveInteger(duration, "Duration") || !isPositiveDouble(price, "Price")) {
            return false;
        }
        return true;
    }

    private boolean isPositiveInteger(String value, String fieldName) {
        try {
            int intValue = Integer.parseInt(value);
            if (intValue <= 0) {
                Toast.makeText(this, fieldName + " must be a positive number.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, fieldName + " must be a valid number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPositiveDouble(String value, String fieldName) {
        try {
            double doubleValue = Double.parseDouble(value);
            if (doubleValue <= 0) {
                Toast.makeText(this, fieldName + " must be a valid positive number.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, fieldName + " must be a valid number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void syncToFirebase(Course course) {
        Log.d("FirebaseSync", "Starting sync to Firebase for course ID: " + course.getId());

        firebaseDbRef.child(String.valueOf(course.getId()))
                .setValue(course)
                .addOnSuccessListener(aVoid -> Log.d("FirebaseSync", "Course synced successfully with ID: " + course.getId()))
                .addOnFailureListener(e -> Log.e("FirebaseSync", "Failed to sync course with ID: " + course.getId(), e));
    }

    private boolean isValidDay(String day) {
        String[] validDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String validDay : validDays) {
            if (validDay.equalsIgnoreCase(day)) {
                return true;
            }
        }
        return false;
    }
}