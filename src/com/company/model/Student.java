package com.company.model;

import java.sql.Date;

public class Student {
    private int id;
    private String name;
    private String courseName;
    private String courseCode;
    private int score;
    private Date date;

    public Student(int id, String name, String courseName, String courseCode, int score, Date date) {
        this.id = id;
        this.name = name;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.score = score;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public boolean isStudentPass(){
        return this.getScore() > 60;
    }

    public String getNameAndScore(){
        return "Student name: " + this.getName() + "    " + "Score: " + this.getScore() + "     " + "Passed: " + this.isStudentPass();
    }

    @Override
    public String toString() {
        return "Student:  " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", courseName = '" + courseName + '\'' +
                ", courseCode = '" + courseCode + '\'' +
                ", score = " + score +
                ", date = " + date;
    }
}
