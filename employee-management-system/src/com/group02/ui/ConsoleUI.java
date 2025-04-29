package com.group02.ui;

import com.group02.model.Employee;
import com.group02.model.DynamicEmployee;
import com.group02.service.DynamicEmployeeServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Text-based UI for the Employee Management System.
 * Presents a menu loop for CRUD + bulk raise, with improved formatting.
 */
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final DynamicEmployeeServiceImpl service;

    public ConsoleUI(DynamicEmployeeServiceImpl service) {
        this.service = service;
    }

    public void run() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getInt("Choice: ");
            switch (choice) {
                case 1 -> add();
                case 2 -> viewAll();
                case 3 -> search();
                case 4 -> update();
                case 5 -> delete();
                case 6 -> bulkRaise();
                case 7 -> addDynamicField();
                case 8 -> updateDynamicFields();
                case 9 -> viewDynamicEmployee();
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        System.out.println("Goodbye!");
    }

    private void displayMenu() {
        System.out.println("\n╔══════════ Employee Manager ══════════╗");
        System.out.println("║ 1) Add        2) View All              ║");
        System.out.println("║ 3) Search     4) Update                ║");
        System.out.println("║ 5) Delete     6) Bulk Raise            ║");
        System.out.println("║ 7) Add Field  8) Update Fields         ║");
        System.out.println("║ 9) View Dynamic Employee  0) Exit      ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private void add() {
        System.out.println("\n-- Add Employee --");
        Employee e = new Employee();
        e.setName(getString("Name: "));
        e.setJobTitle(getString("Job Title: "));
        e.setDivision(getString("Division: "));
        e.setSalary(getDouble("Salary: "));

        String payInfo;
        do {
            payInfo = getString("Pay Info (FULLTIME or PARTTIME): ");
            payInfo = payInfo.toUpperCase();
        } while (!payInfo.equals("FULLTIME") && !payInfo.equals("PARTTIME"));

        e.setPayInfo(payInfo);

        int id = service.add(e);
        System.out.println(id > 0 ? "Added, ID=" + id : "Add failed.");
    }

    private void addDynamicField() {
        System.out.println("\n-- Add Dynamic Field --");
        String fieldName = getString("Field Name: ");
        String dataType = getString("Data Type (VARCHAR(255), INT, DATE, etc.): ");
        String defaultValueStr = getOpt("Default Value (leave empty for null): ");

        Object defaultValue = null;
        if (!defaultValueStr.isEmpty()) {
            // Convert string to appropriate type based on dataType
            if (dataType.toUpperCase().contains("INT")) {
                defaultValue = Integer.parseInt(defaultValueStr);
            } else if (dataType.toUpperCase().contains("DECIMAL") ||
                    dataType.toUpperCase().contains("DOUBLE") ||
                    dataType.toUpperCase().contains("FLOAT")) {
                defaultValue = Double.parseDouble(defaultValueStr);
            } else {
                defaultValue = defaultValueStr;
            }
        }

        boolean success = service.addDynamicField(fieldName, dataType, defaultValue);
        System.out.println(success ? "Field added successfully." : "Failed to add field.");
    }

    private void viewAll() {
        List<Employee> list = service.findAll();
        if (list.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        System.out.printf("%-5s %-20s %-15s %-10s%n",
                "ID", "Name", "Job Title", "Salary");
        System.out.println("---------------------------------------------------------------");
        for (Employee e : list) {
            System.out.printf("%-5d %-20s %-15s $%,10.2f%n",
                    e.getEmpID(), e.getName(),
                    e.getJobTitle(), e.getSalary());
        }
    }

    private void search() {
        System.out.println("\n-- Search --");
        System.out.println("1) By ID   2) By Name   3) By Dynamic Field");
        int o = getInt("Option: ");
        switch (o) {
            case 1 -> {
                int id = getInt("Enter ID: ");
                Optional<Employee> e = service.searchByID(id);
                System.out.println(e.orElse(null));
            }
            case 2 -> {
                String nm = getString("Enter name pattern: ");
                service.searchByName(nm)
                        .forEach(emp -> System.out.println(emp + "\n"));
            }
            case 3 -> {
                searchByDynamicField();
            }
            default -> System.out.println("Invalid search option.");
        }
    }

    private void searchByDynamicField() {
        System.out.println("\n-- Search by Dynamic Field --");

        // Get available dynamic fields
        List<String> fields = service.getKnownDynamicFields();

        if (fields.isEmpty()) {
            System.out.println("No dynamic fields defined yet.");
            return;
        }

        // Display available fields
        System.out.println("Available fields:");
        for (int i = 0; i < fields.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, fields.get(i));
        }

        // Get field selection
        int fieldIndex = -1;
        while (fieldIndex < 0 || fieldIndex >= fields.size()) {
            fieldIndex = getInt("Select field (1-" + fields.size() + "): ") - 1;
        }

        String fieldName = fields.get(fieldIndex);
        String searchValue = getString("Enter search value: ");

        // Ask if pattern search should be used
        String usePatternStr = getString("Use pattern matching? (y/n): ");
        boolean usePattern = usePatternStr.equalsIgnoreCase("y");

        List<DynamicEmployee> results;
        if (usePattern) {
            results = service.searchByDynamicFieldPattern(fieldName, searchValue);
        } else {
            results = service.searchByDynamicField(fieldName, searchValue);
        }

        // Display results
        if (results.isEmpty()) {
            System.out.println("No matching employees found.");
            return;
        }

        System.out.println("\nMatching Employees:");
        System.out.printf("%-5s %-20s %-15s %-10s%n",
                "ID", "Name", "Job Title", fieldName);
        System.out.println("------------------------------------------------------");

        for (DynamicEmployee emp : results) {
            Employee baseEmp = emp.getBaseEmployee();
            Object fieldValue = emp.getField(fieldName);

            System.out.printf("%-5d %-20s %-15s %-10s%n",
                    baseEmp.getEmpID(),
                    baseEmp.getName(),
                    baseEmp.getJobTitle(),
                    fieldValue != null ? fieldValue.toString() : "null");
        }

        // Ask if user wants to view full details of any employee
        String viewFullStr = getString("\nView full details of an employee? (y/n): ");
        if (viewFullStr.equalsIgnoreCase("y")) {
            int empID = getInt("Enter employee ID: ");

            // Find the employee in results
            Optional<DynamicEmployee> empOpt = results.stream()
                    .filter(e -> e.getBaseEmployee().getEmpID() == empID)
                    .findFirst();

            if (empOpt.isPresent()) {
                DynamicEmployee emp = empOpt.get();
                System.out.println("\n" + emp.getBaseEmployee().toString());

                // Display dynamic fields
                Map<String, Object> dynamicFields = emp.getAllDynamicFields();
                if (!dynamicFields.isEmpty()) {
                    System.out.println("\nDynamic Fields:");
                    for (Map.Entry<String, Object> entry : dynamicFields.entrySet()) {
                        System.out.printf("%-20s: %s%n", entry.getKey(), entry.getValue());
                    }
                }
            } else {
                System.out.println("Employee not found in search results.");
            }
        }
    }

    private void update() {
        System.out.println("\n-- Update Employee --");
        int id = getInt("Enter ID: ");
        Optional<Employee> opt = service.searchByID(id);
        if (opt.isEmpty()) {
            System.out.println("Not found.");
            return;
        }
        Employee emp = opt.get();
        System.out.println(emp);

        String nm = getOpt("Name [" + emp.getName() + "]: ");
        if (!nm.isBlank())
            emp.setName(nm);

        String jt = getOpt("Job Title [" + emp.getJobTitle() + "]: ");
        if (!jt.isBlank())
            emp.setJobTitle(jt);

        String dv = getOpt("Division [" + emp.getDivision() + "]: ");
        if (!dv.isBlank())
            emp.setDivision(dv);

        String sl = getOpt("Salary [" + emp.getSalary() + "]: ");
        if (!sl.isBlank())
            emp.setSalary(Double.parseDouble(sl));

        String pi = getOpt("Pay Info [" + emp.getPayInfo() + "]: ");
        if (!pi.isBlank())
            emp.setPayInfo(pi);

        System.out.println(service.update(emp) ? "Updated." : "Update failed.");
    }

    private void updateDynamicFields() {
        System.out.println("\n-- Update Dynamic Fields --");
        int empID = getInt("Employee ID: ");

        Optional<DynamicEmployee> empOpt = service.getDynamicEmployee(empID);
        if (empOpt.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }

        DynamicEmployee emp = empOpt.get();

        // Show employee basic info
        Employee baseEmp = emp.getBaseEmployee();
        System.out.printf("Updating fields for: %s (ID: %d)%n",
                baseEmp.getName(), baseEmp.getEmpID());

        // Get dynamic fields
        Map<String, Object> fields = emp.getAllDynamicFields();
        List<String> knownFields = service.getKnownDynamicFields();

        if (knownFields.isEmpty()) {
            System.out.println("No dynamic fields defined yet.");
            return;
        }

        // Update each field
        for (String fieldName : knownFields) {
            Object currentValue = emp.getField(fieldName);
            String prompt = String.format("%s [%s]: ", fieldName, currentValue);
            String newValueStr = getOpt(prompt);

            if (!newValueStr.isEmpty()) {
                // Convert to appropriate type (simplified)
                Object newValue = newValueStr;
                if (currentValue instanceof Integer) {
                    newValue = Integer.parseInt(newValueStr);
                } else if (currentValue instanceof Double) {
                    newValue = Double.parseDouble(newValueStr);
                }

                emp.setField(fieldName, newValue);
            }
        }

        boolean success = service.updateDynamicEmployee(emp);
        System.out.println(success ? "Updated successfully." : "Update failed.");
    }

    private void viewDynamicEmployee() {
        System.out.println("\n-- View Dynamic Employee --");
        int empID = getInt("Employee ID: ");

        Optional<DynamicEmployee> empOpt = service.getDynamicEmployee(empID);
        if (empOpt.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }

        DynamicEmployee emp = empOpt.get();

        // Display base employee info
        System.out.println(emp.getBaseEmployee().toString());

        // Display dynamic fields
        Map<String, Object> fields = emp.getAllDynamicFields();
        if (!fields.isEmpty()) {
            System.out.println("\nDynamic Fields:");
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                System.out.printf("%-20s: %s%n", entry.getKey(), entry.getValue());
            }
        } else {
            System.out.println("\nNo dynamic fields set for this employee.");
        }
    }

    private void delete() {
        System.out.println("\n-- Delete Employee --");
        int id = getInt("Enter ID to delete: ");
        String confirm = getString("Are you sure? (y/N): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }
        System.out.println(service.delete(id) ? "Deleted." : "No such employee.");
    }

    private void bulkRaise() {
        System.out.println("\n-- Bulk Salary Raise --");
        double min = getDouble("Min Salary: ");
        double max = getDouble("Max Salary: ");
        double pct = getDouble("Raise %: ");
        service.applySalaryRaise(min, max, pct);
        System.out.println("Raise applied.");
    }

    // Input helpers ----------------------------------------------------------

    private String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String getOpt(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Try again.");
            }
        }
    }

    private double getDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}