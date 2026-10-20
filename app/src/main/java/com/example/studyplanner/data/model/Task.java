package com.example.studyplanner.data.model;

public class Task {
    private long id;
    private Integer courseId;
    private String course;
    private String title;
    private String description;
    private String category;   // "EXAM" (Lịch thi), "ASSIGNMENT" (Bài tập), "STUDY" (Ôn tập), "AUTO" (Gợi ý)
    private int status;        // 0: Chưa hoàn thành, 1: Đã hoàn thành
    private int priority;      // 1: Thấp (Xanh), 2: Trung bình (Cam), 3: Cao (Đỏ)
    private String deadline;   // Ngày giờ hạn nộp / thời gian thi
    private int durationMinutes;

    public Task() {}

    public Task(String title, String description, String category, int priority, String deadline, int durationMinutes) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.durationMinutes = durationMinutes;
        this.status = 0;
    }

    public Task(long id, String title, String description, String category, int status, int priority, String deadline, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
        this.durationMinutes = durationMinutes;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}