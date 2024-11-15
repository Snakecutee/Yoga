package com.example.yoga.model;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga.Classes.Class; // Đảm bảo bạn sử dụng Class thay vì Course
import com.example.yoga.DatabaseHelper;
import com.example.yoga.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private EditText editTextSearch;
    private Button buttonSearch;
    private ListView listViewResults;
    private DatabaseHelper db;
    private ArrayList<Class> searchResults; // Sử dụng Class thay vì Course

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Khởi tạo các view
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        listViewResults = findViewById(R.id.listViewResults);

        // Khởi tạo DatabaseHelper
        db = new DatabaseHelper(this);

        // Thiết lập listener cho nút tìm kiếm
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // Xử lý sự kiện nhấn vào item trong ListView
        listViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Class selectedClass = searchResults.get(position);
                // Bạn có thể bắt đầu một Activity chỉnh sửa ở đây với thông tin chi tiết của lớp đã chọn
                Toast.makeText(SearchActivity.this, "Selected Class ID: " + selectedClass.getClassId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm kiếm trong cơ sở dữ liệu theo teacherName
        searchResults = (ArrayList<Class>) db.searchClassesByTeacherName(query);
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No classes found", Toast.LENGTH_SHORT).show();
        } else {
            // Hiển thị kết quả trong ListView
            ArrayAdapter<Class> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults);
            listViewResults.setAdapter(adapter);
        }
    }
}