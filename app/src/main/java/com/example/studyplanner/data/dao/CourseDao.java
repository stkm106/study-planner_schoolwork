package com.example.studyplanner.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studyplanner.data.database.DatabaseHelper;
import com.example.studyplanner.data.model.Course;

import java.util.ArrayList;
import java.util.List;
public class CourseDao {
    private final DatabaseHelper dbHelper;

    public CourseDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 1. Thêm môn học
    public long insert(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", course.getCode());
        values.put("name", course.getName());
        values.put("color_hex", course.getColorHex());

        long id = db.insert("courses", null, values);
        db.close();
        return id;
    }

    // 2. Lấy danh sách tất cả môn học
    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses ORDER BY name ASC", null);

        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("color_hex"))
                );
                list.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 3. Cập nhật môn học
    public int update(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", course.getCode());
        values.put("name", course.getName());
        values.put("color_hex", course.getColorHex());

        int rows = db.update("courses", values, "id = ?", new String[]{String.valueOf(course.getId())});
        db.close();
        return rows;
    }

    // 4. Xóa môn học
    public int delete(int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("courses", "id = ?", new String[]{String.valueOf(courseId)});
        db.close();
        return rows;
    }
}
