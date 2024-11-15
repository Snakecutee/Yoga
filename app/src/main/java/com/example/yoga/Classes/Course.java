package com.example.yoga.Classes;

public class Course {
    private int id;
    private String day;
    private String time;
    private String description;
    private String capacity;
    private String duration;
    private String price;
    private String classType;

    public Course(String day, String time, String capacity, String duration, String price, String classType, String description) {
        this.id = 0;  // Initialize id to 0 (it will be assigned later)
        this.day = day;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.description = description;
    }

    // Constructor with id (used when updating a course)
    public Course(int id, String day, String time, String capacity, String duration, String price, String classType, String description) {
        this.id = id;
        this.day = day;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getDuration() {
        return duration;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getClassType() {
        return classType;
    }
}
