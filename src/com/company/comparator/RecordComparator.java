package com.company.comparator;

import com.company.model.Student;

import java.util.Comparator;

public class RecordComparator implements Comparator<Student> {
    @Override
    public int compare(Student o1, Student o2) {
        return o1.getScore() - o2.getScore();
    }
}
