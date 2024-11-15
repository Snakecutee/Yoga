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

import com.example.yoga.Classes.Class;
import com.example.yoga.DatabaseHelper;
import com.example.yoga.R;
import com.example.yoga.model.EditClassActivity;

import java.util.List;

public class ClassListAdapter extends ArrayAdapter<Class> {
    private Context context;
    private List<Class> classyoga;
    private DatabaseHelper db;

    public ClassListAdapter(Context context, List<Class> classs) {
        super(context, 0, classs);
        this.context = context;
        this.classyoga = classs;
        this.db = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Class classs = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.class_item, parent, false);
        }

        TextView courseDetailsText = convertView.findViewById(R.id.textViewClassName);
        Button deleteButton = convertView.findViewById(R.id.buttonDeleteClass);
        Button editButton = convertView.findViewById(R.id.buttonEditClass);
        courseDetailsText.setText("Class " + (position + 1));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditClassActivity.class);
                intent.putExtra("CLASS_ID", classs.getClassId());
                intent.putExtra("teacher_name", classs.getTeacherName());
                intent.putExtra("class_day", classs.getDay());
                intent.putExtra("comments", classs.getComments());
                intent.putExtra("course_id", classs.getCourseId());
                context.startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete this course?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            int classId = classs.getClassId();
                            db.deleteClass(classId);
                            classyoga.remove(classs);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Course deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        return convertView;
    }
}
