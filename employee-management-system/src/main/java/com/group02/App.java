package com.group02;

import com.group02.model.Employee;
import com.group02.repository.EmployeeRepository;
import com.group02.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        System.out.println("Initializing application...");

        try {
            // Initialize database schema if needed
            initializeDatabase();

            // Example: Create a new user
            EmployeeRepository employeeRepo = new EmployeeRepository();
            // Employee(String name, String SSN, String jobTitle, String division, double
            // salary, String payInfo)
            Employee newEmployee = new Employee("johndoe", "123456789", "Engineer", "5th", 2000, "FullTime");
            int empID = employeeRepo.save(newEmployee);

            System.out.println("Created user with ID: " + empID);

            // Retrieve and display all users
            System.out.println("All users:");
            employeeRepo.findAll().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close connection pool when app terminates
            DatabaseUtil.closeDataSource();
        }
    }

    private static void initializeDatabase() {
        // Create tables if they don't exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "email VARCHAR(255) NOT NULL UNIQUE" +
                ")";

        try (Connection conn = DatabaseUtil.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSQL);
            System.out.println("Database schema initialized.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}