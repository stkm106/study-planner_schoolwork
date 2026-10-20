package com.example.studyplanner.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public long dateTimeMillis;
    public String type;        // "EXAM", "ASSIGNMENT", "STUDY"
    public int durationMinutes;
    public int priority;
    public boolean isCompleted;

    public TaskEntity(String title, String description, long dateTimeMillis, String type, int durationMinutes, int priority) {
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.priority = priority;
        this.isCompleted = false;
    }
}