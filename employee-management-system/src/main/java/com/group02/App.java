package com.group02;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import com.group02.config.DatabaseConfig;

import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeManager employeeManager = new EmployeeManager();

    public static void main(String[] args) {
        try {
            DatabaseConfig.loadProperties("config.properties");
            DatabaseConfig.initializeDatabaseSchema();
            System.out.println("Initializing database connection...");
            DatabaseConfig.initializeConnectionPool();
            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Exit if we can't connect to the database
        }

        boolean running = true;

        System.out.println("==== Employee Management System ====");
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    // viewAllEmployees();
                    break;
                case 3:
                    // searchEmployee();
                    break;
                case 4:
                    // updateEmployee();
                    break;
                case 5:
                    // deleteEmployee();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting the application. Goodbye!");
                    DatabaseConfig.closeDataSource();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Add New Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Search Employees");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("0. Exit");
    }

    private static void addEmployee() {
        System.out.println("\n=== Add New Employee ===");

        Employee employee = new Employee();

        employee.setName(getStringInput("Enter employee name: "));
        employee.setSSN(getStringInput("Enter SSN (9 digits): "));
        employee.setJobTitle(getStringInput("Enter job title: "));
        employee.setDivision(getStringInput("Enter division: "));
        employee.setSalary(getDoubleInput("Enter salary: "));
        employee.setPayInfo(getStringInput("Enter payment info: "));

        int empID = employeeManager.addEmployee(employee);

        if (empID > 0) {
            System.out.println("Employee added successfully with ID: " + empID);
        } else {
            System.out.println("Failed to add employee. Please try again.");
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine().trim());
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double input = Double.parseDouble(scanner.nextLine().trim());
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /*
     * public static void main(String[] args) {
     * System.out.println("Initializing application...");
     * boolean running = true;
     * initializeDatabase();
     * 
     * //
     * 
     * try {
     * // Add the rest of the app here
     * 
     * /*
     * EmployeeManager employeeManager = new EmployeeManager();
     * Employee newEmployee = new Employee("johndoe", "123456789", "Engineer",
     * "5th", 2000, "FullTime");
     * 
     * 
     * } catch (Exception e) {
     * e.printStackTrace();
     * } finally {
     * // Close connection pool when app terminates
     * DatabaseConfig.closeDataSource();
     * }
     * }
     * 
     * private static void initializeDatabase() {
     * // Run Flyway migrations
     * DatabaseConfig.initializeDatabaseSchema();
     * // initialize connection pool
     * DatabaseConfig.initializeConnectionPool();
     * }
     */
}