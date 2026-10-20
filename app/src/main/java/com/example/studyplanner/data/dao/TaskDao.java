package com.example.studyplanner.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studyplanner.data.database.DatabaseHelper;
import com.example.studyplanner.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_DEADLINE = "deadline";

    public TaskDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_STATUS, task.getStatus());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_DEADLINE, task.getDeadline());

        return db.insert(TABLE_NAME, null, values);
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE));

                list.add(new Task(id, title, desc, category, status, priority, deadline, 90));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public void updateTaskStatus(long taskId, boolean isCompleted) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, isCompleted ? 1 : 0);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
    }
}