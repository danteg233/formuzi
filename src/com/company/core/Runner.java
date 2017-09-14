package com.company.core;


import com.company.data_readers.DatabaseReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Runner {

    //TODO: U SHOULD CREATE MYSQL TABLE USE THIS COMMAND (DATABASE'S SHOULD BE student)
    //TODO: TABLE NAME WILL BE 'students' AND SET PASSWORD AS 'test'
    /*
    delimiter $$
    CREATE TABLE `students` (
      `id` int(11) NOT NULL,
      `name` varchar(45) DEFAULT NULL,
      `courseName` varchar(45) NOT NULL,
      `courseCode` varchar(45) DEFAULT NULL,
      `score` int(11) DEFAULT NULL,
      `date` date DEFAULT NULL,
      PRIMARY KEY (`id`,`courseName`),
      UNIQUE KEY `id_UNIQUE` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8$$
     */

    private static String url = "jdbc:mysql://localhost:3306/student";
    private static String username = "root";
    private static String password = "test";
    private static Connection connection;

    public static void main(String[] args) {
        //TODO : HERE ADD MENU
        try{
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected...");
            DatabaseReader reader = new DatabaseReader(connection);
            reader.readAllRecords();
            reader.addRecord();
            reader.deleteRecord();
            reader.modifyRecord();
            reader.updateScore();
            reader.getListOfStudentsByCourseName();
            reader.getScoreByInput();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try { connection.close(); } catch(SQLException se) { /*can't do anything */ }
        }


    }


}
