package com.example.studyplanner.data.model;

public class Task {
    private int id;
    private Integer courseId; // Nullable
    private String title;
    private String description;
    private String category; // "ASSIGNMENT", "EXAM", "HOMEWORK", "PROJECT"...
    private int status; // 0 = Pending, 1 = Completed
    private int priority; // 1 = Low, 2 = Medium, 3 = High
    private String deadline; // YYYY-MM-DD HH:MM:SS
    private String location;
    private String course;

    public Task() {}

    public Task(int id, Integer courseId, String title, String description, String category, int status, int priority, String deadline, String location) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.priority = priority;
        this.deadline = deadline;
        this.location = location;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

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

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}