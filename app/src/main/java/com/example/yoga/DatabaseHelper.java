package com.example.yoga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yoga.Classes.Class;
import com.example.yoga.Classes.Course;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "yoga2.db";
    private static final int DATABASE_VERSION = 1;
    private DatabaseReference firebaseDbRef;
    private static final String TABLE_COURSE = "course";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DAY = "day";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CLASS_TYPE = "class_type";
    private static final String COLUMN_DESCRIPTION = "description";
//class
private  static final String TABLE_CLASS = "class";
    private  static final String COLUMN_CLASS_ID = "class_Id";
    private  static final String COLUMN_CLASS_DAY = "class_day";
    private  static final String COLUMN_CLASS_TEACHER = "teacher_name";
    private  static final String COLUMN_COMMENTS = "comments";
    private  static  final String COLUMN_COURSE_ID = "course_id";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Course
        String createTable = "CREATE TABLE " + TABLE_COURSE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DAY + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_CAPACITY + " TEXT, "
                + COLUMN_DURATION + " TEXT, "
                + COLUMN_PRICE + " TEXT, "
                + COLUMN_CLASS_TYPE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(createTable);

        //Class
        String createClassTable = "CREATE TABLE " + TABLE_CLASS +"("
         + COLUMN_CLASS_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
         + COLUMN_CLASS_TEACHER + " TEXT, "
         + COLUMN_CLASS_DAY + " TEXT, "
         + COLUMN_COMMENTS + " TEXT, "
         + COLUMN_COURSE_ID + " INTEGER, "

        + "FOREIGN KEY(" + COLUMN_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_ID + ")  ON DELETE CASCADE)";

        db.execSQL(createClassTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {  // If the version is lower than the new version
            // Add the missing column
            db.execSQL("ALTER TABLE " + TABLE_COURSE + " ADD COLUMN " + COLUMN_CLASS_TYPE + " TEXT;");
            db.execSQL("ALTER TABLE " +  TABLE_CLASS  + " ADD COLUMN " + COLUMN_CLASS_TYPE + " TEXT;");
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE );
    }
    public long insertClass(Class newClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_id", newClass.getCourseId());
        values.put("class_day", newClass.getDay());
        values.put("teacher_name", newClass.getTeacherName());
        values.put("comments", newClass.getComments());

        long result = db.insert("class", null, values);
        db.close();

        if (result != -1) {

            newClass.setClassId((int) result);
            firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("classes");
            firebaseDbRef.child(String.valueOf(newClass.getClassId())).setValue(newClass)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("FirebaseSync", "Class synced to Firebase with ID: " + newClass.getClassId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseSync", "Failed to sync class to Firebase: " + e.getMessage());
                    });

            Log.d("SQLiteInsert", "Class inserted successfully with ID: " + result);
        } else {
            Log.e("SQLiteInsert", "Failed to insert class into SQLite.");
        }
        return result;
    }

    public long insertCourse(Course course) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, course.getDay());
        values.put(COLUMN_TIME, course.getTime());
        values.put(COLUMN_CAPACITY, course.getCapacity());
        values.put(COLUMN_DURATION, course.getDuration());
        values.put(COLUMN_PRICE, course.getPrice());
        values.put(COLUMN_CLASS_TYPE, course.getClassType());
        values.put(COLUMN_DESCRIPTION, course.getDescription());

        long result = db.insert(TABLE_COURSE, null, values);
        db.close();

        if (result != -1) {
            course.setId((int) result);  // Set SQLite generated ID to course object
            firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("courses");
            String firebaseCourseId = String.valueOf(course.getId());  // Use SQLite ID as Firebase ID
            firebaseDbRef.child(firebaseCourseId).setValue(course)
                    .addOnSuccessListener(aVoid -> {
                        // Success
                        Log.d("FirebaseSync", "Course synced to Firebase with ID: " + firebaseCourseId);
                    })
                    .addOnFailureListener(e -> {
                        // Failure
                        Log.e("FirebaseSync", "Failed to sync course to Firebase: " + e.getMessage());
                    });

            Log.d("SQLiteInsert", "Course inserted successfully with ID: " + result);
        } else {
            // Insertion failed
            Log.e("SQLiteInsert", "Failed to insert course into SQLite.");
        }
        return result;
    }


    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COURSE, null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                );
                courseList.add(course);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return courseList;
    }
    public void deleteCourse(int courseId) {
        // Xóa khóa học từ SQLite
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_COURSE, COLUMN_ID + " = ?", new String[]{String.valueOf(courseId)});
        db.close();

        // Kiểm tra nếu việc xóa trong SQLite thành công
        if (rowsAffected > 0) {
            // Xóa khóa học từ Firebase
            firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("courses");
            firebaseDbRef.child(String.valueOf(courseId)).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Thành công: Khóa học đã xóa thành công từ Firebase
                        Log.d("FirebaseSync", "Course deleted from Firebase with ID: " + courseId);
                        // Có thể thêm thông báo Toast hoặc UI update ở đây
                    })
                    .addOnFailureListener(e -> {
                        // Thất bại: Không xóa được khóa học từ Firebase
                        Log.e("FirebaseSync", "Failed to delete course from Firebase: " + e.getMessage());
                        // Thực hiện hành động khi xóa thất bại từ Firebase (có thể thông báo lỗi cho người dùng)
                    });

            // Thành công trong việc xóa khóa học từ SQLite
            Log.d("SQLiteDelete", "Course deleted successfully from SQLite with ID: " + courseId);
            // Có thể thêm Toast hoặc UI update ở đây để thông báo người dùng đã xóa thành công
        } else {
            // Nếu không xóa được trong SQLite, không thể tiếp tục xóa từ Firebase
            Log.e("SQLiteDelete", "Failed to delete course from SQLite with ID: " + courseId);

        }
    }

    public boolean updateCourse(Course course) {
        // Kiểm tra nếu ID không hợp lệ
        if (course.getId() == -1) {
            Log.e("updateCourse", "Invalid course ID");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DAY, course.getDay());
        contentValues.put(COLUMN_TIME, course.getTime());
        contentValues.put(COLUMN_CAPACITY, course.getCapacity());
        contentValues.put(COLUMN_DURATION, course.getDuration());
        contentValues.put(COLUMN_PRICE, course.getPrice());
        contentValues.put(COLUMN_CLASS_TYPE, course.getClassType());
        contentValues.put(COLUMN_DESCRIPTION, course.getDescription());

        // Cập nhật khóa học với ID hợp lệ
        int rowsUpdated = db.update(TABLE_COURSE, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(course.getId())});
        db.close();

        if (rowsUpdated > 0) {
            Log.d("updateCourse", "Course updated successfully with ID: " + course.getId());


            syncToFirebase(course);
            return true;
        } else {
            Log.e("updateCourse", "Failed to update course with ID: " + course.getId());
            return false;
        }
    }

    private void syncToFirebase(Course course) {
        // Đồng bộ hóa khóa học lên Firebase
        DatabaseReference firebaseDbRef = FirebaseDatabase.getInstance().getReference("courses");
        firebaseDbRef.child(String.valueOf(course.getId()))  // Sử dụng course ID làm key trong Firebase
                .setValue(course)  // Cập nhật khóa học mới
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSync", "Course synced successfully with ID: " + course.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseSync", "Failed to sync course with ID: " + course.getId(), e);
                });
    }

    public List<Class> getAllClasses(int courseId) {
        List<Class> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + COLUMN_COURSE_ID + " = ?",
                new String[]{String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("class_day"));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("class_Id"));
                int courseID = cursor.getInt(cursor.getColumnIndexOrThrow("course_id"));



                Class classs = new Class(id,courseID,teacherName,day,comments);
                classs.setCourseId(courseID);
                classs.setClassId(id);
                classList.add(classs);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }
    public Class getClassById(int classId,int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("class",
                null,
                "class_Id = ?",
                new String[]{String.valueOf(classId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"));
            String classDay = cursor.getString(cursor.getColumnIndexOrThrow("class_day"));
            String comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"));

            Class classs = new Class(classId, courseId, teacherName, classDay, comments);
            cursor.close();
            return classs;
        }

        if (cursor != null) cursor.close();
        return null; // Không tìm thấy lớp học
    }
    public void deleteClass(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("class", "class_id = ?", new String[]{String.valueOf(classId)});
        db.close();

        if (rowsAffected > 0) {
            Log.d("SQLiteDelete", "Class deleted successfully from SQLite with ID: " + classId);
            deleteClassFromFirebase(classId);
        } else {
            Log.e("SQLiteDelete", "Failed to delete class from SQLite with ID: " + classId);
        }
    }
    private void deleteClassFromFirebase(int classId) {
        firebaseDbRef = FirebaseDatabase.getInstance("https://yoga1-1f935-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("classes");
        firebaseDbRef.child(String.valueOf(classId)).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSync", "Course deleted from Firebase with ID: " + classId);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseSync", "Failed to delete course from Firebase: " + e.getMessage());
                });
    }
    public List<Class> searchClassesByTeacherName(String teacherName) {
        List<Class> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_CLASS,
                null,
                "teacher_name LIKE ?",
                new String[]{"%" + teacherName + "%"},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                String teacherNameFromDb = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("class_day"));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow("comments"));
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("class_Id"));
                int courseID = cursor.getInt(cursor.getColumnIndexOrThrow("course_id"));

                Class classs = new Class(id, courseID, teacherNameFromDb, day, comments);
                classs.setCourseId(courseID);
                classs.setClassId(id);
                classList.add(classs);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return classList;
    }


}