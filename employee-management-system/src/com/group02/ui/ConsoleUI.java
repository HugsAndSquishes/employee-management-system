package com.group02.ui;

import com.group02.model.DynamicEmployee;
import com.group02.model.Employee;
import com.group02.service.DynamicEmployeeService;
import com.group02.service.DynamicEmployeeServiceImpl;
import com.group02.service.EmployeeService;
import com.group02.service.EmployeeServiceImpl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private EmployeeService employeeService;
    private DynamicEmployeeService dynamicEmployeeService;
    private Scanner scanner;

    public ConsoleUI() {
        this.employeeService = new EmployeeServiceImpl();
        this.dynamicEmployeeService = new DynamicEmployeeServiceImpl();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayAllEmployees();
                    break;
                case "2":
                    searchEmployee();
                    break;
                case "3":
                    addEmployee();
                    break;
                case "4":
                    updateEmployee();
                    break;
                case "5":
                    deleteEmployee();
                    break;
                case "6":
                    updateSalariesInRange();
                    break;
                case "7":
                    reportsMenu();
                    break;
                case "8":
                    addColumn();
                    break;
                case "9":
                    running = false;
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n========== EMPLOYEE MANAGEMENT SYSTEM ==========");
        System.out.println("1. Display All Employees");
        System.out.println("2. Search Employee");
        System.out.println("3. Add Employee");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Update Salaries in Range");
        System.out.println("7. Reports");
        System.out.println("8. Add Column to Employee Table");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    private void displayAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found in the database.");
            return;
        }

        System.out.println("\n========== ALL EMPLOYEES ==========");
        for (Employee employee : employees) {
            System.out.println(employee);
            System.out.println("-----------------------------------");
        }
    }

    private void searchEmployee() {
        System.out.println("\n========== SEARCH EMPLOYEE ==========");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Search by Job Title");
        System.out.println("4. Search by Division");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine();
        Map<String, Object> criteria = new HashMap<>();

        switch (choice) {
            case "1":
                System.out.print("Enter Employee ID: ");
                int id = Integer.parseInt(scanner.nextLine());
                criteria.put("empID", id);
                break;
            case "2":
                System.out.print("Enter Employee Name: ");
                String name = scanner.nextLine();
                criteria.put("employeeName", name);
                break;
            case "3":
                System.out.print("Enter Job Title: ");
                String jobTitle = scanner.nextLine();
                criteria.put("jobTitle", jobTitle);
                break;
            case "4":
                System.out.print("Enter Division: ");
                String division = scanner.nextLine();
                criteria.put("division", division);
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        List<Employee> employees = employeeService.searchEmployees(criteria);

        if (employees.isEmpty()) {
            System.out.println("No employees found matching the criteria.");
            return;
        }

        System.out.println("\n========== SEARCH RESULTS ==========");
        for (Employee employee : employees) {
            System.out.println(employee);
            System.out.println("-----------------------------------");
        }
    }

    private void addEmployee() {
        System.out.println("\n========== ADD EMPLOYEE ==========");

        System.out.print("Enter Employee Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Job Title: ");
        String jobTitle = scanner.nextLine();

        System.out.print("Enter Division: ");
        String division = scanner.nextLine();

        System.out.print("Enter Salary: ");
        BigDecimal salary = new BigDecimal(scanner.nextLine());

        System.out.print("Enter Pay Info (FULLTIME or PARTTIME): ");
        String payInfo = scanner.nextLine().toUpperCase();
        if (!payInfo.equals("FULLTIME") && !payInfo.equals("PARTTIME")) {
            System.out.println("Invalid pay info. Must be FULLTIME or PARTTIME.");
            return;
        }

        Employee employee = new Employee(0, name, jobTitle, division, salary, payInfo);

        if (employeeService.addEmployee(employee)) {
            System.out.println("Employee added successfully with ID: " + employee.getEmpID());
        } else {
            System.out.println("Failed to add employee.");
        }
    }

    private void updateEmployee() {
        System.out.println("\n========== UPDATE EMPLOYEE ==========");

        System.out.print("Enter Employee ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            System.out.println("Employee not found with ID: " + id);
            return;
        }

        System.out.println("Current Employee Details:");
        System.out.println(employee);

        System.out.println("\nEnter new details (press Enter to keep current value):");

        System.out.print("Enter Employee Name [" + employee.getEmployeeName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            employee.setEmployeeName(name);
        }

        System.out.print("Enter Job Title [" + employee.getJobTitle() + "]: ");
        String jobTitle = scanner.nextLine();
        if (!jobTitle.isEmpty()) {
            employee.setJobTitle(jobTitle);
        }

        System.out.print("Enter Division [" + employee.getDivision() + "]: ");
        String division = scanner.nextLine();
        if (!division.isEmpty()) {
            employee.setDivision(division);
        }

        System.out.print("Enter Salary [" + employee.getSalary() + "]: ");
        String salaryStr = scanner.nextLine();
        if (!salaryStr.isEmpty()) {
            employee.setSalary(new BigDecimal(salaryStr));
        }

        System.out.print("Enter Pay Info (FULLTIME or PARTTIME) [" + employee.getPayInfo() + "]: ");
        String payInfo = scanner.nextLine().toUpperCase();
        if (!payInfo.isEmpty()) {
            if (!payInfo.equals("FULLTIME") && !payInfo.equals("PARTTIME")) {
                System.out.println("Invalid pay info. Must be FULLTIME or PARTTIME.");
                return;
            }
            employee.setPayInfo(payInfo);
        }

        // Check if there are dynamic attributes to update
        Map<String, String> columns = dynamicEmployeeService.getTableColumns();
        DynamicEmployee dynamicEmployee = dynamicEmployeeService.getDynamicEmployeeById(id);

        if (dynamicEmployee != null) {
            for (Map.Entry<String, String> column : columns.entrySet()) {
                String columnName = column.getKey();

                // Skip standard columns
                if (columnName.equals("empID") || columnName.equals("employeeName") ||
                        columnName.equals("jobTitle") || columnName.equals("division") ||
                        columnName.equals("salary") || columnName.equals("payInfo")) {
                    continue;
                }

                Object currentValue = dynamicEmployee.getAttribute(columnName);
                System.out.print("Enter " + columnName + " [" + currentValue + "]: ");
                String value = scanner.nextLine();

                if (!value.isEmpty()) {
                    // Try to convert the string value to the appropriate type
                    if (column.getValue().contains("INT")) {
                        dynamicEmployee.addAttribute(columnName, Integer.parseInt(value));
                    } else if (column.getValue().contains("DECIMAL") || column.getValue().contains("DOUBLE")) {
                        dynamicEmployee.addAttribute(columnName, new BigDecimal(value));
                    } else {
                        dynamicEmployee.addAttribute(columnName, value);
                    }
                }
            }

            if (dynamicEmployeeService.updateDynamicEmployee(dynamicEmployee)) {
                System.out.println("Employee updated successfully.");
            } else {
                System.out.println("Failed to update employee.");
            }
        } else {
            if (employeeService.updateEmployee(employee)) {
                System.out.println("Employee updated successfully.");
            } else {
                System.out.println("Failed to update employee.");
            }
        }
    }

    private void deleteEmployee() {
        System.out.println("\n========== DELETE EMPLOYEE ==========");

        System.out.print("Enter Employee ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            System.out.println("Employee not found with ID: " + id);
            return;
        }

        System.out.println("Employee to delete:");
        System.out.println(employee);

        System.out.print("Are you sure you want to delete this employee? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("y")) {
            if (employeeService.deleteEmployee(id)) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    private void updateSalariesInRange() {
        System.out.println("\n========== UPDATE SALARIES IN RANGE ==========");

        System.out.print("Enter minimum salary: ");
        BigDecimal minSalary = new BigDecimal(scanner.nextLine());

        System.out.print("Enter maximum salary: ");
        BigDecimal maxSalary = new BigDecimal(scanner.nextLine());

        System.out.print("Enter percentage increase (e.g., 3.2 for 3.2%): ");
        double percentageIncrease = Double.parseDouble(scanner.nextLine());

        if (percentageIncrease <= 0) {
            System.out.println("Percentage increase must be positive.");
            return;
        }

        System.out.println("This will update salaries for all employees with salary >= " +
                minSalary + " and < " + maxSalary + " by " + percentageIncrease + "%");
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("y")) {
            if (employeeService.updateSalariesInRange(minSalary, maxSalary, percentageIncrease)) {
                System.out.println("Salaries updated successfully.");
            } else {
                System.out.println("Failed to update salaries or no employees in the specified range.");
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }

    private void reportsMenu() {
        boolean inReportsMenu = true;

        while (inReportsMenu) {
            System.out.println("\n========== REPORTS ==========");
            System.out.println("1. Full-time Employee Information");
            System.out.println("2. Total Pay by Job Title");
            System.out.println("3. Total Pay by Division");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showFullTimeEmployees();
                    break;
                case "2":
                    showTotalPayByJobTitle();
                    break;
                case "3":
                    showTotalPayByDivision();
                    break;
                case "4":
                    inReportsMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void showFullTimeEmployees() {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("payInfo", "FULLTIME");

        List<Employee> employees = employeeService.searchEmployees(criteria);

        if (employees.isEmpty()) {
            System.out.println("No full-time employees found.");
            return;
        }

        System.out.println("\n========== FULL-TIME EMPLOYEES ==========");
        for (Employee employee : employees) {
            System.out.println(employee);
            System.out.println("-----------------------------------");
        }
    }

    private void showTotalPayByJobTitle() {
        Map<String, BigDecimal> totalPayByJobTitle = employeeService.getTotalPayByJobTitle();

        if (totalPayByJobTitle.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        System.out.println("\n========== TOTAL PAY BY JOB TITLE ==========");
        for (Map.Entry<String, BigDecimal> entry : totalPayByJobTitle.entrySet()) {
            System.out.printf("%-30s $%,.2f%n", entry.getKey(), entry.getValue());
        }
    }

    private void showTotalPayByDivision() {
        Map<String, BigDecimal> totalPayByDivision = employeeService.getTotalPayByDivision();

        if (totalPayByDivision.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        System.out.println("\n========== TOTAL PAY BY DIVISION ==========");
        for (Map.Entry<String, BigDecimal> entry : totalPayByDivision.entrySet()) {
            System.out.printf("%-30s $%,.2f%n", entry.getKey(), entry.getValue());
        }
    }

    private void addColumn() {
        System.out.println("\n========== ADD COLUMN TO EMPLOYEE TABLE ==========");

        // Print current columns
        Map<String, String> currentColumns = dynamicEmployeeService.getTableColumns();
        System.out.println("Current columns in employee table:");
        for (Map.Entry<String, String> entry : currentColumns.entrySet()) {
            System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
        }

        System.out.print("\nEnter new column name: ");
        String columnName = scanner.nextLine().trim();

        if (columnName.isEmpty()) {
            System.out.println("Column name cannot be empty.");
            return;
        }

        if (currentColumns.containsKey(columnName)) {
            System.out.println("Column already exists.");
            return;
        }

        System.out.println("Select data type:");
        System.out.println("1. VARCHAR(255) - Text");
        System.out.println("2. INT - Integer");
        System.out.println("3. DECIMAL(10,2) - Decimal number");
        System.out.println("4. DATE - Date");
        System.out.print("Enter your choice: ");

        String choice = scanner.nextLine();
        String dataType;

        switch (choice) {
            case "1":
                dataType = "VARCHAR(255)";
                break;
            case "2":
                dataType = "INT";
                break;
            case "3":
                dataType = "DECIMAL(10,2)";
                break;
            case "4":
                dataType = "DATE";
                break;
            default:
                System.out.println("Invalid option. Using VARCHAR(255) by default.");
                dataType = "VARCHAR(255)";
                break;
        }

        if (dynamicEmployeeService.addColumn(columnName, dataType)) {
            System.out.println("Column added successfully.");
        } else {
            System.out.println("Failed to add column.");
        }
    }
}