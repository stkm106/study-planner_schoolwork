package com.example.studyplanner.data.database; // Kiểm tra đúng package của project m

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper; // Đảm bảo có dòng import này

// LƯU Ý PHẢI CÓ: extends SQLiteOpenHelper
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudyPlanner.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Bảng Courses
        db.execSQL("CREATE TABLE courses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "color_hex TEXT DEFAULT '#2196F3')");

        // 2. Bảng Assignments
        db.execSQL("CREATE TABLE assignments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "deadline TEXT NOT NULL, " +
                "priority INTEGER DEFAULT 2, " +
                "status INTEGER DEFAULT 0, " +
                "estimated_hours REAL DEFAULT 1.0, " +
                "FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE SET NULL)");

        // 3. Bảng Examinations
        db.execSQL("CREATE TABLE examinations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER, " +
                "title TEXT NOT NULL, " +
                "exam_date TEXT NOT NULL, " +
                "location TEXT, " +
                "duration_minutes INTEGER DEFAULT 60, " +
                "FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE)");

        // 4. Bảng Study Schedules
        db.execSQL("CREATE TABLE study_schedules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER, " +
                "day_of_week INTEGER NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "is_auto_generated INTEGER DEFAULT 0, " +
                "FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE)");

        // 5. Bảng Study Logs
        db.execSQL("CREATE TABLE study_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "assignment_id INTEGER, " +
                "session_type TEXT DEFAULT 'POMODORO', " +
                "duration_minutes INTEGER NOT NULL, " +
                "completed_at TEXT NOT NULL, " +
                "FOREIGN KEY(assignment_id) REFERENCES assignments(id) ON DELETE SET NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS study_logs");
        db.execSQL("DROP TABLE IF EXISTS study_schedules");
        db.execSQL("DROP TABLE IF EXISTS examinations");
        db.execSQL("DROP TABLE IF EXISTS assignments");
        db.execSQL("DROP TABLE IF EXISTS courses");
        onCreate(db);
    }
}