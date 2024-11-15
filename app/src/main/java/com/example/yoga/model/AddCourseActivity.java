package com.example.yoga.model;

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

public class AddCourseActivity extends BaseActivity {

    private EditText dayOfWeek, timeOfCourse, capacity, duration, price, description;
    private RadioGroup classTypeGroup;
    private Button addButton;
    private DatabaseHelper db;
    private DatabaseReference firebaseDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate AddCourseActivity layout into BaseActivity content frame
        getLayoutInflater().inflate(R.layout.activity_add_course, findViewById(R.id.content_frame));

        // Initialize views
        dayOfWeek = findViewById(R.id.addTextDayOfWeek);
        timeOfCourse = findViewById(R.id.addTextTimeOfCourse);
        capacity = findViewById(R.id.addTextCapacity);
        duration = findViewById(R.id.addTextDuration);
        price = findViewById(R.id.addTextPrice);
        classTypeGroup = findViewById(R.id.addclassTypeGroup);
        description = findViewById(R.id.addTextDescription);
        addButton = findViewById(R.id.addButton);

        // Initialize database helper and Firebase reference
        db = new DatabaseHelper(this);
        firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("courses");

        // Set click listener for the Add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });
    }

    private void addCourse() {
        // Get input values
        String day = dayOfWeek.getText().toString().trim();
        String time = timeOfCourse.getText().toString().trim();
        String courseCapacity = capacity.getText().toString().trim();
        String courseDuration = duration.getText().toString().trim();
        String coursePrice = price.getText().toString().trim();
        String courseDescription = description.getText().toString().trim();


        int selectedClassTypeId = classTypeGroup.getCheckedRadioButtonId();
        RadioButton selectedClassTypeButton = findViewById(selectedClassTypeId);
        String classType = selectedClassTypeButton != null ? selectedClassTypeButton.getText().toString() : "";


        if (day.isEmpty() || !isValidDay(day)) {
            Toast.makeText(this, "Please enter a valid day of the week.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (time.isEmpty() || !time.matches("^(0[1-9]|1[0-9]|2[0-4]):([0-5][0-9])$")) {
            Toast.makeText(this, "Please enter a valid time (e.g., 10:00).", Toast.LENGTH_SHORT).show();
            return;
        }


        if (courseCapacity.isEmpty() || courseDuration.isEmpty() || coursePrice.isEmpty() || classType.isEmpty()) {
            Toast.makeText(this, "All required fields must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int capacityValue = Integer.parseInt(courseCapacity);
            if (capacityValue <= 0) {
                Toast.makeText(this, "Capacity must be a positive number.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Capacity must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int durationValue = Integer.parseInt(courseDuration);
            if (durationValue <= 0) {
                Toast.makeText(this, "Duration must be a positive number.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duration must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double priceValue = Double.parseDouble(coursePrice);
            if (priceValue <= 0) {
                Toast.makeText(this, "Price must be a valid positive number.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price must be a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        Course newCourse = new Course (day, time, courseCapacity, courseDuration, coursePrice,classType, courseDescription );
        long result = db.insertCourse(newCourse);
        if (result == -1) {
            Toast.makeText(this, "Failed to add course to SQLite.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            newCourse.setId((int) result);
            Toast.makeText(this, "Course added to SQLite successfully!", Toast.LENGTH_SHORT).show();
        }
        syncToFirebase(newCourse);
        clearFields();
    }

    private void syncToFirebase(Course course) {
        // Use the courseId from SQLite for Firebase
        String courseId = String.valueOf(course.getId());  // Use the SQLite generated ID (id from SQLite is passed directly)

        // Now use the same ID for Firebase
        firebaseDbRef.child(courseId).setValue(course)
                .addOnSuccessListener(aVoid -> {
                    // Success
                    Toast.makeText(this, "Course synced to Firebase!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failure
                    Log.e("FirebaseError", "Failed to sync course: " + e.getMessage());
                    Toast.makeText(this, "Failed to sync course to Firebase.", Toast.LENGTH_SHORT).show();
                });
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
    private void clearFields() {
        dayOfWeek.setText("");
        timeOfCourse.setText("");
        capacity.setText("");
        duration.setText("");
        price.setText("");
        classTypeGroup.clearCheck();
        description.setText("");
    }

}
