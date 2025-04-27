package com.group02;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import com.group02.config.DatabaseConfig;
import java.util.Scanner;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeManager employeeManager = new EmployeeManager();

    public static void main(String[] args) {
        try {
            System.out.println("Loading database properties...");
            DatabaseConfig.loadProperties(null);

            System.out.println("Initializing database schema (migrations)...");
            DatabaseConfig.initializeDatabaseSchema();

            System.out.println("Initializing connection pool...");
            DatabaseConfig.initializeConnectionPool();
            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
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
                    viewAllEmployees();
                    break;
                case 3:
                    searchEmployee();
                    break;
                case 4:
                    updateEmployeeMenu();
                    break;
                case 5:
                    deleteEmployeeMenu();
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

    private static void viewAllEmployees() {
        System.out.println("\n=== All Employees ===");
        List<Employee> employees = employeeManager.findAll();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
        } else {
            employees.forEach(e -> System.out.println(e + "\n"));
        }
    }

    private static void searchEmployee() {
        System.out.println("\n=== Search Employees ===");
        System.out.println("Search by: 1. ID  2. Name  3. SSN");
        int option = getIntInput("Choose option: ");
        switch (option) {
            case 1:
                int id = getIntInput("Enter employee ID: ");
                Optional<Employee> byId = employeeManager.searchByID(id);
                if (byId.isPresent()) System.out.println(byId.get());
                else System.out.println("No employee found with ID: " + id);
                break;
            case 2:
                String name = getStringInput("Enter name pattern: ");
                List<Employee> byName = employeeManager.searchByName(name);
                if (byName.isEmpty()) System.out.println("No employees found matching name: " + name);
                else byName.forEach(e -> System.out.println(e + "\n"));
                break;
            case 3:
                String ssn = getStringInput("Enter SSN (9 digits): ");
                Optional<Employee> bySsn = employeeManager.searchBySSN(ssn);
                if (bySsn.isPresent()) System.out.println(bySsn.get());
                else System.out.println("No employee found with SSN: " + ssn);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void updateEmployeeMenu() {
        System.out.println("\n=== Update Employee ===");
        int id = getIntInput("Enter employee ID to update: ");
        Optional<Employee> opt = employeeManager.searchByID(id);
        if (opt.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }
        Employee employee = opt.get();
        System.out.println("Current details:\n" + employee + "\n");
        System.out.println("Enter new values (leave blank to keep current):");
        String name = getOptionalString("Name [" + employee.getName() + "]: ");
        if (!name.isBlank()) employee.setName(name);
        String ssn = getOptionalString("SSN [" + employee.getSSN() + "]: ");
        if (!ssn.isBlank()) employee.setSSN(ssn);
        String job = getOptionalString("Job Title [" + employee.getJobTitle() + "]: ");
        if (!job.isBlank()) employee.setJobTitle(job);
        String div = getOptionalString("Division [" + employee.getDivision() + "]: ");
        if (!div.isBlank()) employee.setDivision(div);
        String salStr = getOptionalString("Salary [" + employee.getSalary() + "]: ");
        if (!salStr.isBlank()) {
            try { employee.setSalary(Double.parseDouble(salStr)); }
            catch (NumberFormatException e) { System.out.println("Invalid salary. Keeping old value."); }
        }
        String payInfo = getOptionalString("Pay Info [" + employee.getPayInfo() + "]: ");
        if (!payInfo.isBlank()) employee.setPayInfo(payInfo);
        boolean success = employeeManager.updateEmployee(employee);
        System.out.println(success ? "Employee updated successfully." : "Failed to update employee.");
    }

    private static void deleteEmployeeMenu() {
        System.out.println("\n=== Delete Employee ===");
        int id = getIntInput("Enter employee ID to delete: ");
        boolean success = employeeManager.deleteEmployee(id);
        System.out.println(success ? "Employee deleted." : "Failed to delete employee. (Maybe not found)");
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static String getOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
