package com.company.data_readers;

import com.company.comparator.RecordComparator;
import com.company.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseReader {
    private Connection connection;
    private String selectStatement = "SELECT * FROM student.students;";
    private String addStatement = "insert into student.students values (?, ?, ?, ?, ?, ?);";
    private String deleteStatement = "DELETE FROM student.students WHERE (name = ? AND id = ?);";
    private String modifyStatement = "UPDATE student.students SET id = ?, name = ? , courseName = ?, courseCode = ?, score = ?, date = ?" +
            " WHERE (name = ? AND id = ?);";
    private String updateScore = "UPDATE student.students SET score = ? " +
            "WHERE (name = ? AND id = ?);";
    private String getAllRecordsStatement = "SELECT * FROM student.students WHERE courseName = ?";
    private String fuzzyStatement = "SELECT * from student.students" +
            " WHERE ? LIKE ";
    private static Scanner scanner = new Scanner(System.in);
    private List students = new ArrayList<>();
    private Date date;
    private int id;
    private String name;
    private String courseName;
    private String courseCode;
    private int score;

    public Connection getConnection() {
        return connection;
    }

    public DatabaseReader(Connection connection) {
        this.connection = connection;
    }

    public void readAllRecords() {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = getConnection().prepareStatement(selectStatement);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    System.out.println(new Student(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5), resultSet.getDate(6)).toString());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addRecord(){
        PreparedStatement preparedStatement;
        inputData();
        try{
            preparedStatement = getConnection().prepareStatement(addStatement);
            setStatementFields(preparedStatement);
            if(preparedStatement.executeUpdate() != 0){
                System.out.println("Student record has been added...");
            }else {
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.err.println("Student with this name already exist in the table!");
        }
    }

    public void deleteRecord(){
        PreparedStatement preparedStatement;
        System.out.println("Enter id: ");
        id = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter name: ");
        name = scanner.nextLine();
        try{
            preparedStatement = getConnection().prepareStatement(deleteStatement);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(1, name);
            if(preparedStatement.executeUpdate() != 0){
                System.out.println("Student record with name " + name + " has been successfully deleted...");
            }else{
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.err.println("Could't delete student with name " + name + ". Check your input data!");
        }
    }

    public void modifyRecord(){
        PreparedStatement preparedStatement;
        System.out.println("Enter search id: ");
        int searchId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter search name: ");
        String searchName = scanner.nextLine();
        System.out.println("---Enter new data for student---");
        inputData();
        try{
            preparedStatement = getConnection().prepareStatement(modifyStatement);
            setStatementFields(preparedStatement);
            preparedStatement.setString(7, searchName);
            preparedStatement.setInt(8, searchId);
            if(preparedStatement.executeUpdate() != 0){
                System.out.println("Student record with name " + searchName + " has been successfully modified...");
            }else{
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.err.println("Could't find student with name " + name + ". Check your input data!");
        }
    }

    public void updateScore(){
        PreparedStatement preparedStatement;
        System.out.println("Enter id: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter name: ");
        String name = scanner.nextLine();
        System.out.println("Enter new score: ");
        String newScore = scanner.nextLine();
        try{
            preparedStatement = getConnection().prepareStatement(updateScore);
            preparedStatement.setString(1, newScore);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, id);
            if(preparedStatement.executeUpdate() != 0){
                System.out.println("Student's score has been successfully modified...");
            }else{
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.err.println("Could't modify student's score. Check your input data!");
        }
    }

    public void getListOfStudentsByCourseName(){
        int passed = 0, failed = 0;
        System.out.println("Enter name of course: ");
        String courseName = scanner.nextLine();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            preparedStatement = getConnection().prepareStatement(getAllRecordsStatement);
            preparedStatement.setString(1, courseName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    students.add(new Student(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5), resultSet.getDate(6)));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            students.sort(new RecordComparator());
            for (Object student : students) {
                System.out.println(((Student) student).getNameAndScore());
                if(((Student) student).isStudentPass()){
                    passed++;
                }else{
                    failed++;
                }
            }
            System.out.println("\n\nTOTAL STUDENTS: " + students.size() + "     PASSED: " + passed + "      FAILED: " + failed);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void getScoreByInput(){
        System.out.println("Search by (name, id, course code): ");
        String searchField = scanner.nextLine();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String name , id, courseCode;
        switch (searchField){
            case "id" : System.out.println("Enter id: ");
                        id = scanner.nextLine();
                        fuzzyStatement += id;
                        fuzzyStatement = fuzzyStatement.replace("?", "id").replace(id, "'" + id + "%'");
                        break;

            case "name": System.out.println("Enter name: ");
                         name = scanner.nextLine();
                         fuzzyStatement += name;
                         fuzzyStatement = fuzzyStatement.replace("?", "name").replace(name, "'" + name + "%'");
                         break;

            case "course code": System.out.println("Enter course code: ");
                courseCode = scanner.nextLine();
                fuzzyStatement += courseCode;
                fuzzyStatement = fuzzyStatement.replace("?", "courseCode").replace(courseCode, "'" + courseCode + "%'");
                break;

            default: System.err.println("Wrong input...");
        }

        try {
            preparedStatement = getConnection().prepareStatement(fuzzyStatement);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    students.add(new Student(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5), resultSet.getDate(6)));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            students.sort(new RecordComparator());
            for (Object student : students) {
                System.out.println(student.toString());
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void inputData(){
        System.out.println("Enter id: ");
        id = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter name: ");
        name = scanner.nextLine();
        System.out.println("Enter course name: ");
        courseName = scanner.nextLine();
        System.out.println("Course code: ");
        courseCode = scanner.nextLine();
        System.out.println("Enter score: ");
        score = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter date: ");
        date = Date.valueOf(scanner.nextLine());
    }

    private void setStatementFields(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, courseName);
        preparedStatement.setString(4, courseCode);
        preparedStatement.setInt(5, score);
        preparedStatement.setDate(6, date);
    }
}
