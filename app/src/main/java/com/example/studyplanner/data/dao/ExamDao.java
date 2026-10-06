package com.example.studyplanner.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studyplanner.data.database.DatabaseHelper;
import com.example.studyplanner.data.model.Examination;

import java.util.ArrayList;
import java.util.List;
public class ExamDao {
    private final DatabaseHelper dbHelper;

    public ExamDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 1. Thêm lịch thi
    public long insert(Examination exam) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_id", exam.getCourseId());
        values.put("title", exam.getTitle());
        values.put("exam_date", exam.getExamDate());
        values.put("location", exam.getLocation());
        values.put("duration_minutes", exam.getDurationMinutes());

        long id = db.insert("examinations", null, values);
        db.close();
        return id;
    }

    // 2. Lấy tất cả lịch thi (sắp xếp theo ngày thi gần nhất)
    public List<Examination> getAllExams() {
        List<Examination> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM examinations ORDER BY exam_date ASC", null);

        if (cursor.moveToFirst()) {
            do {
                Examination exam = new Examination(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("course_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("exam_date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("duration_minutes"))
                );
                list.add(exam);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 3. Cập nhật lịch thi
    public int update(Examination exam) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("course_id", exam.getCourseId());
        values.put("title", exam.getTitle());
        values.put("exam_date", exam.getExamDate());
        values.put("location", exam.getLocation());
        values.put("duration_minutes", exam.getDurationMinutes());

        int rows = db.update("examinations", values, "id = ?", new String[]{String.valueOf(exam.getId())});
        db.close();
        return rows;
    }

    // 4. Xóa lịch thi
    public int delete(int examId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("examinations", "id = ?", new String[]{String.valueOf(examId)});
        db.close();
        return rows;
    }
}
