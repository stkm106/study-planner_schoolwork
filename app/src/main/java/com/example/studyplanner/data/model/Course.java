package com.example.studyplanner.data.model;

public class Course {
    private int id;
    private String code;
    private String name;
    private String colorHex;

    public Course() {}

    public Course(int id, String code, String name, String colorHex) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.colorHex = colorHex;
    }

    public Course(String code, String name, String colorHex) {
        this.code = code;
        this.name = name;
        this.colorHex = colorHex;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}