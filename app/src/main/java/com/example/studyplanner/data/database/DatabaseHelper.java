package com.example.studyplanner.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "study_planner.db";
    private static final int DATABASE_VERSION = 2; // Nâng version để cập nhật bảng

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Bảng courses
        String createCoursesTable = "CREATE TABLE courses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "code TEXT, " +
                "name TEXT, " +
                "color_hex TEXT)";

        // 2. Bảng tasks (Thêm cột course kiểu TEXT)
        String createTasksTable = "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER, " +
                "course TEXT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "category TEXT NOT NULL, " +
                "status INTEGER DEFAULT 0, " +
                "priority INTEGER DEFAULT 2, " +
                "deadline TEXT, " +
                "location TEXT, " +
                "FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE SET NULL)";

        // 3. Bảng study_schedules
        String createSchedulesTable = "CREATE TABLE study_schedules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_id INTEGER, " +
                "day_of_week INTEGER, " +
                "start_time TEXT, " +
                "end_time TEXT, " +
                "is_auto_generated INTEGER DEFAULT 0, " +
                "FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE)";

        // 4. Bảng study_logs
        String createLogsTable = "CREATE TABLE study_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_id INTEGER, " +
                "course_id INTEGER, " +
                "session_type TEXT, " +
                "duration_minutes INTEGER, " +
                "completed_at TEXT, " +
                "FOREIGN KEY(task_id) REFERENCES tasks(id) ON DELETE SET NULL, " +
                "FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE SET NULL)";

        db.execSQL(createCoursesTable);
        db.execSQL(createTasksTable);
        db.execSQL(createSchedulesTable);
        db.execSQL(createLogsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS study_logs");
        db.execSQL("DROP TABLE IF EXISTS study_schedules");
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS courses");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}