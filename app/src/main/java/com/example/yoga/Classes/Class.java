package com.example.yoga.Classes;

public class Class {
    private int classId;
    private int courseId;
    private String day;
    private String teacherName;
    private String comments;
    public Class() {
    }


    public Class(int classId, int courseId, String teacherName,String day,  String comments) {
        this.classId = classId;
        this.courseId = courseId;
        this.day = day;

        this.teacherName = teacherName;
        this.comments = comments;
    }


    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }


    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}

