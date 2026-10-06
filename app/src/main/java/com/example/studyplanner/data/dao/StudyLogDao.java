package com.example.studyplanner.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studyplanner.data.database.DatabaseHelper;
import com.example.studyplanner.data.model.StudyLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StudyLogDao {
    private final DatabaseHelper dbHelper;

    public StudyLogDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // thêm nhật ký học tập mới
    public long insertLog(StudyLog log) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (log.getAssignmentId() != null) {
            values.put("assignment_id", log.getAssignmentId());
        } else {
            values.putNull("assignment_id");
        }
        values.put("session_type", log.getSessionType() != null ? log.getSessionType() : "POMODORO");
        values.put("duration_minutes", log.getDurationMinutes());
        values.put("completed_at", log.getCompletedAt());

        long id = db.insert("study_logs", null, values);
        db.close();
        return id;
    }

    // tổng số phút đã học hôm nay
    public int getTotalMinutesToday() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String query = "SELECT SUM(duration_minutes) FROM study_logs WHERE completed_at LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{today + "%"});

        int totalMinutes = 0;
        if (cursor.moveToFirst()) {
            totalMinutes = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return totalMinutes;
    }

    // thống kê theo tuần dạng "YYYY-MM-DD" -> Số phút
    public Map<String, Integer> getWeeklyStudyMinutes() {
        Map<String, Integer> weeklyData = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query lấy tổng số phút gom nhóm theo ngày trong 7 ngày gần nhất
        String query = "SELECT DATE(completed_at) as study_date, SUM(duration_minutes) as total_mins " +
                "FROM study_logs " +
                "WHERE completed_at >= DATE('now', '-6 days') " +
                "GROUP BY DATE(completed_at)";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                int mins = cursor.getInt(1);
                weeklyData.put(date, mins);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return weeklyData;
    }
}
