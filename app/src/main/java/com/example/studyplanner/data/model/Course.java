package com.example.studyplanner.data.model;

public class Course {
    private int id;
    private String code;
    private String name;
    private String colorHex;

    // 1. Constructor rỗng
    public Course() {}

    // 2. Constructor không có id (Dùng khi INSERT mới)
    public Course(String code, String name, String colorHex) {
        this.code = code;
        this.name = name;
        this.colorHex = colorHex;
    }

    // 3. Constructor đầy đủ có id (Dùng khi SELECT từ SQLite)
    public Course(int id, String code, String name, String colorHex) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.colorHex = colorHex;
    }
    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}