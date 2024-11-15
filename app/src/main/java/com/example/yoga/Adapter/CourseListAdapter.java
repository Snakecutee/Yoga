package com.example.yoga.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoga.Classes.Course;
import com.example.yoga.index.Classyoga;
import com.example.yoga.DatabaseHelper;
import com.example.yoga.R;
import com.example.yoga.model.EditCourseActivity;

import java.util.List;

public class CourseListAdapter extends ArrayAdapter<Course> {
    private Context context;
    private List<Course> courses;
    private DatabaseHelper db;

    public CourseListAdapter(Context context, List<Course> courses) {
        super(context, 0, courses);
        this.context = context;
        this.courses = courses;
        this.db = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Course course = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.course_list_item, parent, false);
        }

        TextView courseDetailsText = convertView.findViewById(R.id.courseDetailsText);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button editButton = convertView.findViewById(R.id.editButton);
        Button moreButton = convertView.findViewById(R.id.moreButton);

        courseDetailsText.setText(course.getDay() + " - " + course.getPrice() + " $");

        // Xử lý sự kiện xóa khóa học
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete this course?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Xóa khóa học khỏi cơ sở dữ liệu
                            db.deleteCourse(course.getId());
                            courses.remove(course);  // Loại bỏ khóa học khỏi danh sách
                            notifyDataSetChanged();  // Cập nhật lại giao diện
                            Toast.makeText(context, "Course deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        // Xử lý sự kiện "More"
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gửi khóa học ID đến Classyoga
                Intent intent = new Intent(context, Classyoga.class);
                intent.putExtra("course_id", course.getId()); // Truyền khóa học ID
                context.startActivity(intent);
            }
        });

        // Xử lý sự kiện sửa khóa học
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = course.getDescription();
                String classType = course.getClassType();
                Intent intent = new Intent(context, EditCourseActivity.class);
                intent.putExtra("course_id", course.getId());
                intent.putExtra("day", course.getDay());
                intent.putExtra("time", course.getTime());
                intent.putExtra("capacity", course.getCapacity());
                intent.putExtra("duration", course.getDuration());
                intent.putExtra("price", course.getPrice());
                intent.putExtra("description", description);
                intent.putExtra("classType", classType);

                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
