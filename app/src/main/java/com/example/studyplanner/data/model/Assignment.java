package com.example.studyplanner.data.model;

public class Assignment {
    private int id;
    private int courseId;
    private String title;
    private String description;
    private String deadline; // Lưu chuỗi định dạng ISO-8601
    private int priority;
    private int status;
    private double estimatedHours;

    public Assignment() {}

    // 2. Constructor không có id (Dùng khi INSERT mới)
    public Assignment(int courseId, String title, String description, String deadline, int priority, int status, double estimatedHours) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.estimatedHours = estimatedHours;
    }

    // 3. Constructor đầy đủ có id (Dùng khi SELECT từ SQLite)
    public Assignment(int id, int courseId, String title, String description, String deadline, int priority, int status, double estimatedHours) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.estimatedHours = estimatedHours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }
}