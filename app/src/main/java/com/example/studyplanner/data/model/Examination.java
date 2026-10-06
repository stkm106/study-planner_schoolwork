package com.example.studyplanner.data.model;

public class Examination {
    private int id;
    private int courseId;
    private String title;
    private String examDate;
    private String location;
    private int durationMinutes;

    // 1. Constructor rỗng
    public Examination() {}

    // 2. Constructor không có id (Dùng khi INSERT mới)
    public Examination(int courseId, String title, String examDate, String location, int durationMinutes) {
        this.courseId = courseId;
        this.title = title;
        this.examDate = examDate;
        this.location = location;
        this.durationMinutes = durationMinutes;
    }

    // 3. Constructor đầy đủ có id (Dùng khi SELECT từ SQLite)
    public Examination(int id, int courseId, String title, String examDate, String location, int durationMinutes) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.examDate = examDate;
        this.location = location;
        this.durationMinutes = durationMinutes;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
