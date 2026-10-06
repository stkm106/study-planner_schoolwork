package com.example.studyplanner.data.model;

public class StudyLog {
    private int id;
    private Integer assignmentId; // Dùng Integer để chấp nhận null nếu học tự do không gắn bài tập
    private String sessionType;
    private int durationMinutes;
    private String completedAt; // Định dạng ISO-8601 YYYY-MM-DD HH:mm:ss

    public StudyLog() {}

    public StudyLog(Integer assignmentId, String sessionType, int durationMinutes, String completedAt) {
        this.assignmentId = assignmentId;
        this.sessionType = sessionType;
        this.durationMinutes = durationMinutes;
        this.completedAt = completedAt;
    }

    public StudyLog(int id, Integer assignmentId, String sessionType, int durationMinutes, String completedAt) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.sessionType = sessionType;
        this.durationMinutes = durationMinutes;
        this.completedAt = completedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}
