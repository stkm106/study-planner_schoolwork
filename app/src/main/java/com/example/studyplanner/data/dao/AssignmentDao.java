package com.example.studyplanner.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studyplanner.data.database.DatabaseHelper;
import com.example.studyplanner.data.model.Assignment;

import java.util.ArrayList;
import java.util.List;
public class AssignmentDao {
    private final DatabaseHelper dbHelper;

    public AssignmentDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 1. Thêm bài tập
    public long insert(Assignment assignment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_id", assignment.getCourseId() > 0 ? assignment.getCourseId() : null);
        values.put("title", assignment.getTitle());
        values.put("description", assignment.getDescription());
        values.put("deadline", assignment.getDeadline());
        values.put("priority", assignment.getPriority());
        values.put("status", assignment.getStatus());
        values.put("estimated_hours", assignment.getEstimatedHours());

        long id = db.insert("assignments", null, values);
        db.close();
        return id;
    }

    // 2. Lấy tất cả bài tập (sắp xếp theo deadline gần nhất)
    public List<Assignment> getAllAssignments() {
        List<Assignment> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM assignments ORDER BY deadline ASC", null);

        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("course_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("estimated_hours"))
                );
                list.add(assignment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 3. Lấy bài tập theo môn học
    public List<Assignment> getAssignmentsByCourse(int courseId) {
        List<Assignment> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM assignments WHERE course_id = ? ORDER BY deadline ASC", new String[]{String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("course_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("estimated_hours"))
                );
                list.add(assignment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 4. Cập nhật bài tập
    public int update(Assignment assignment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_id", assignment.getCourseId() > 0 ? assignment.getCourseId() : null);
        values.put("title", assignment.getTitle());
        values.put("description", assignment.getDescription());
        values.put("deadline", assignment.getDeadline());
        values.put("priority", assignment.getPriority());
        values.put("status", assignment.getStatus());
        values.put("estimated_hours", assignment.getEstimatedHours());

        int rows = db.update("assignments", values, "id = ?", new String[]{String.valueOf(assignment.getId())});
        db.close();
        return rows;
    }

    // 5. Cập nhật riêng trạng thái hoàn thành (Tích chọn checkbox)
    public int updateStatus(int assignmentId, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);

        int rows = db.update("assignments", values, "id = ?", new String[]{String.valueOf(assignmentId)});
        db.close();
        return rows;
    }

    // 6. Xóa bài tập
    public int delete(int assignmentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("assignments", "id = ?", new String[]{String.valueOf(assignmentId)});
        db.close();
        return rows;
    }
}
