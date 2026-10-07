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
    private DatabaseHelper dbHelper;

    public CourseDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", course.getCode());
        values.put("name", course.getName());
        values.put("color_hex", course.getColorHex());

        return db.insert("courses", null, values);
    }

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses", null);

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
        return list;
    }

    public Course getCourseById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses WHERE id = ?", new String[]{String.valueOf(id)});
        Course course = null;
        if (cursor.moveToFirst()) {
            course = new Course(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("code")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("color_hex"))
            );
        }
        cursor.close();
        return course;
    }

    public int updateCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code", course.getCode());
        values.put("name", course.getName());
        values.put("color_hex", course.getColorHex());

        return db.update("courses", values, "id = ?", new String[]{String.valueOf(course.getId())});
    }

    public int deleteCourse(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("courses", "id = ?", new String[]{String.valueOf(id)});
    }
}