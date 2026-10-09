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
    private DatabaseHelper dbHelper;

    public TaskDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (task.getCourseId() != null) {
            values.put("course_id", task.getCourseId());
        } else {
            values.putNull("course_id");
        }
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("category", task.getCategory());
        values.put("status", task.getStatus());
        values.put("priority", task.getPriority());
        values.put("deadline", task.getDeadline());
        values.put("location", task.getLocation());

        return db.insert("tasks", null, values);
    }

    public List<Task> getTasksByCategory(String category) {
        List<Task> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tasks WHERE category = ? ORDER BY deadline ASC", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                Integer courseId = cursor.isNull(cursor.getColumnIndexOrThrow("course_id")) ?
                        null : cursor.getInt(cursor.getColumnIndexOrThrow("course_id"));

                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        courseId,
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("status")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location"))
                );
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (task.getCourseId() != null) {
            values.put("course_id", task.getCourseId());
        } else {
            values.putNull("course_id");
        }
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("category", task.getCategory());
        values.put("status", task.getStatus());
        values.put("priority", task.getPriority());
        values.put("deadline", task.getDeadline());
        values.put("location", task.getLocation());

        return db.update("tasks", values, "id = ?", new String[]{String.valueOf(task.getId())});
    }

    public int deleteTask(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("tasks", "id = ?", new String[]{String.valueOf(id)});
    }

    // Lấy thông tin 1 Task theo ID để đổ lên Pop-up Edit
    public Task getTaskById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Task task = null;

        Cursor cursor = db.query("tasks", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            task.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
            task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow("priority")));
            task.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow("deadline")));
            task.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("status")));
            cursor.close();
        }

        return task;
    }
}