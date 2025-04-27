package com.group02;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;

import com.group02.config.DatabaseConfig;
import com.group02.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        System.out.println("Initializing application...");
        initializeDatabase();

        try {
            // Add the rest of the app here

            /*
             * EmployeeManager employeeManager = new EmployeeManager();
             * Employee newEmployee = new Employee("johndoe", "123456789", "Engineer",
             * "5th", 2000, "FullTime");
             */

            insertTestData();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close connection pool when app terminates
            DatabaseConfig.closeDataSource();
        }
    }

    private static void insertTestData() {
        EmployeeManager employeeManager = new EmployeeManager();
        String[][] testData = {
                { "John Doe", "123456789", "Engineer", "5th", "2000", "FullTime" },
                { "Jane Smith", "987654321", "Manager", "HR", "3000", "FullTime" },
                { "Michael Brown", "112233445", "Technician", "IT", "1500", "PartTime" },
                { "Alice White", "556677889", "Accountant", "Finance", "2500", "FullTime" },
                { "Bob Green", "667788990", "HR Specialist", "HR", "1800", "FullTime" },
                { "Charlie Black", "998877665", "Developer", "IT", "2200", "Contract" },
                { "David Blue", "112244668", "Designer", "Marketing", "2100", "FullTime" },
                { "Eve Grey", "223344556", "Product Manager", "Product", "2800", "FullTime" },
                { "Frank Yellow", "334455667", "Engineer", "R&D", "2600", "FullTime" },
                { "Grace Red", "445566778", "Intern", "HR", "800", "Intern" }
        };

        for (String[] data : testData) {
            String name = data[0];
            String ssn = data[1];
            String jobTitle = data[2];
            String division = data[3];
            double salary = Double.parseDouble(data[4]); // Convert salary from String to double
            String employmentType = data[5];

            // Create and add employee to the list
            Employee newEmployee = new Employee(name, ssn, jobTitle, division, salary, employmentType);
            int empID = employeeManager.add(newEmployee);

            System.out.println("Created user with ID: " + empID);
        }

        // Retrieve and display all users
        System.out.println("All users:");
        employeeManager.findAll().forEach(System.out::println);
    }

    private static void initializeDatabase() {
        // Run Flyway migrations
        DatabaseConfig.initializeDatabaseSchema();
        // initialize connection pool
        DatabaseConfig.initializeConnectionPool();

        /*
         * // Create tables if they don't exist
         * String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
         * "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
         * "username VARCHAR(255) NOT NULL UNIQUE," +
         * "email VARCHAR(255) NOT NULL UNIQUE" +
         * ")";
         * 
         * try (Connection conn = DatabaseUtil.getConnection();
         * Statement stmt = conn.createStatement()) {
         * 
         * stmt.execute(createTableSQL);
         * System.out.println("Database schema initialized.");
         * 
         * } catch (Exception e) {
         * e.printStackTrace();
         * }
         */
    }
}