package com.example.studyplanner.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.studyplanner.data.database.DatabaseHelper;
import com.example.studyplanner.data.model.Task;
import com.example.studyplanner.data.model.Course;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseSyncHelper {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void syncLocalToCloud(String userId, Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // 1. Lấy danh sách Courses trực tiếp từ SQLite bằng Cursor
        List<Course> courses = new ArrayList<>();
        Cursor cursorCourses = database.rawQuery("SELECT * FROM courses", null);
        if (cursorCourses != null) {
            while (cursorCourses.moveToNext()) {
                int id = cursorCourses.getInt(cursorCourses.getColumnIndexOrThrow("id"));
                String code = cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("code"));
                String name = cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("name"));
                String colorHex = cursorCourses.getString(cursorCourses.getColumnIndexOrThrow("color_hex"));

                Course course = new Course(id, code, name, colorHex); // Đảm bảo model Course có constructor này hoặc dùng setter tương ứng
                courses.add(course);
            }
            cursorCourses.close();
        }

        // Đẩy Courses lên Firestore
        for (Course course : courses) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", course.getCode());
            map.put("name", course.getName());
            map.put("color_hex", course.getColorHex());

            db.collection("users").document(userId)
                    .collection("courses").document(String.valueOf(course.getId()))
                    .set(map);
        }

        // 2. Lấy danh sách Tasks trực tiếp từ SQLite bằng Cursor
        List<Task> tasks = new ArrayList<>();
        Cursor cursorTasks = database.rawQuery("SELECT * FROM tasks", null);
        if (cursorTasks != null) {
            while (cursorTasks.moveToNext()) {
                int id = cursorTasks.getInt(cursorTasks.getColumnIndexOrThrow("id"));
                int courseId = cursorTasks.getInt(cursorTasks.getColumnIndexOrThrow("course_id"));
                String title = cursorTasks.getString(cursorTasks.getColumnIndexOrThrow("title"));
                String description = cursorTasks.getString(cursorTasks.getColumnIndexOrThrow("description"));
                String category = cursorTasks.getString(cursorTasks.getColumnIndexOrThrow("category"));
                int status = cursorTasks.getInt(cursorTasks.getColumnIndexOrThrow("status"));
                int priority = cursorTasks.getInt(cursorTasks.getColumnIndexOrThrow("priority"));
                String deadline = cursorTasks.getString(cursorTasks.getColumnIndexOrThrow("deadline"));
                String location = cursorTasks.getString(cursorTasks.getColumnIndexOrThrow("location"));

                Task task = new Task(id, courseId, title, description, category, status, priority, deadline, location);
                tasks.add(task);
            }
            cursorTasks.close();
        }

        // Đẩy Tasks lên Firestore
        for (Task task : tasks) {
            Map<String, Object> map = new HashMap<>();
            map.put("course_id", task.getCourseId());
            map.put("title", task.getTitle());
            map.put("description", task.getDescription());
            map.put("category", task.getCategory());
            map.put("status", task.getStatus());
            map.put("priority", task.getPriority());
            map.put("deadline", task.getDeadline());
            map.put("location", task.getLocation());

            db.collection("users").document(userId)
                    .collection("tasks").document(String.valueOf(task.getId()))
                    .set(map);
        }

        database.close();
    }
}