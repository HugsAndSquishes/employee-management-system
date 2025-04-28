package com.group02;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import com.group02.config.DatabaseConfig;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeManager employeeManager = new EmployeeManager();

    public static void main(String[] args) {
        try {
            System.out.println("Initializing database...");
            DatabaseConfig.loadProperties(null); // Use default config
            DatabaseConfig.initializeDatabase();
            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
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
                    searchEmployee();
                    break;
                case 4:
                    updateEmployee();
                    break;
                case 5:
                    // deleteEmployee();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting the application!");
                    DatabaseConfig.closeConnection();
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

    private static void searchEmployee() {
        System.out.println("\n=== Search Employees ===");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Search by SSN");
        System.out.println("4. Search by Division");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                searchByID();
                break;
            case 2:
                searchByName();
                break;
            case 3:
                searchBySSN();
                break;
            case 4:

                /*
                 * case 100:
                 * searchByDivision();
                 * break;
                 */
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void searchByID() {
        int empID = getIntInput("Enter employee ID: ");

        Optional<Employee> employeeOpt = employeeManager.searchByID(empID);

        if (employeeOpt.isPresent()) {
            System.out.println("\n=== Employee Found ===");
            displayEmployeeDetails(employeeOpt.get());
        } else {
            System.out.println("No employee found with ID: " + empID);
        }
    }

    private static void searchByName() {
        String name = getStringInput("Enter employee name (or part of name): ");

        List<Employee> employees = employeeManager.searchByName(name);

        if (employees.isEmpty()) {
            System.out.println("No employees found with name containing: " + name);
            return;
        }

        System.out.println("\n=== Employees Found ===");
        printEmployeeTable(employees);
    }

    private static void searchBySSN() {
        String ssn = getStringInput("Enter employee SSN: ");

        Optional<Employee> employeeOpt = employeeManager.searchBySSN(ssn);

        if (employeeOpt.isPresent()) {
            System.out.println("\n=== Employee Found ===");
            displayEmployeeDetails(employeeOpt.get());
        } else {
            System.out.println("No employee found with SSN: " + ssn);
        }
    }

    /*
     * private static void searchByDivision() {
     * String division = getStringInput("Enter division: ");
     * 
     * List<Employee> employees = employeeManager.findByDivision(division);
     * 
     * if (employees.isEmpty()) {
     * System.out.println("No employees found in division: " + division);
     * return;
     * }
     * 
     * System.out.println("\n=== Employees in " + division + " Division ===");
     * printEmployeeTable(employees);
     * }
     */

    private static void updateEmployee() {
        System.out.println("\n=== Update Employee ===");
        int empID = getIntInput("Enter employee ID to update: ");

        Optional<Employee> employeeOpt = employeeManager.searchByID(empID);

        if (!employeeOpt.isPresent()) {
            System.out.println("No employee found with ID: " + empID);
            return;
        }

        Employee employee = employeeOpt.get();
        displayEmployeeDetails(employee);

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Update all fields");
        System.out.println("2. Update name");
        System.out.println("3. Update job title & division");
        System.out.println("4. Update salary");
        System.out.println("5. Update SSN");
        System.out.println("6. Update payment info");

        int choice = getIntInput("Enter your choice: ");

        boolean success = false;

        switch (choice) {
            case 1:
                employee.setName(getStringInput("Enter new name [" + employee.getName() + "]: "));
                employee.setSSN(getStringInput("Enter new SSN [" + employee.getSSN() + "]: "));
                employee.setJobTitle(getStringInput("Enter new job title [" + employee.getJobTitle() + "]: "));
                employee.setDivision(getStringInput("Enter new division [" + employee.getDivision() + "]: "));
                employee.setSalary(getDoubleInput("Enter new salary [" + employee.getSalary() + "]: "));
                employee.setPayInfo(getStringInput("Enter new payment info [" + employee.getPayInfo() + "]: "));

                success = employeeManager.updateEmployee(employee);
                break;
            case 2:
                String name = getStringInput("Enter new name [" + employee.getName() + "]: ");
                success = employeeManager.updateField(empID, "employeeName", name);
                break;
            case 3:
                String jobTitle = getStringInput("Enter new job title [" + employee.getJobTitle() + "]: ");
                String division = getStringInput("Enter new division [" + employee.getDivision() + "]: ");
                success = employeeManager.updateField(empID, "division", division);
                success = employeeManager.updateField(empID, "jobTitle", jobTitle);
                break;
            case 4:
                double salary = getDoubleInput("Enter new salary [" + employee.getSalary() + "]: ");
                success = employeeManager.updateField(empID, "salary", salary);
                break;
            case 5:
                String ssn = getStringInput("Enter new SSN [" + employee.getSSN() + "]: ");
                success = employeeManager.updateField(empID, "SSN", ssn);
                break;
            case 6:
                String payInfo = getStringInput("Enter new payment info [" + employee.getInfo() + "]: ");
                success = employeeManager.updateField(empID, "payInfo", payInfo);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        if (success) {
            System.out.println("Employee updated successfully.");
        } else {
            System.out.println("Failed to update employee.");
        }
    }

    private static void displayEmployeeDetails(Employee employee) {
        System.out.println(employee);
        /*
         * System.out.println("ID: " + employee.getEmpID());
         * System.out.println("Name: " + employee.getName());
         * System.out.println("SSN: " + employee.getSSN());
         * System.out.println("Job Title: " + employee.getJobTitle());
         * System.out.println("Division: " + employee.getDivision());
         * System.out.println("Salary: $" + employee.getSalary());
         * System.out.println("Payment Info: " + employee.getInfo());
         */
    }

    private static void printEmployeeTable(List<Employee> employees) {
        // Print header
        System.out.println("ID\tName\t\tJob Title\tDivision\tSalary");
        System.out.println("----------------------------------------------------------");

        // Print each employee
        for (Employee emp : employees) {
            System.out.printf("%-5d\t%-15s\t%-15s\t%-10s\t$%.2f%n",
                    emp.getEmpID(),
                    truncateString(emp.getName(), 15),
                    truncateString(emp.getJobTitle(), 15),
                    truncateString(emp.getDivision(), 10),
                    emp.getSalary());
        }
    }

    private static String truncateString(String str, int maxLength) {
        if (str == null)
            return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
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