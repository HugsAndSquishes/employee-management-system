// src/main/java/com/group02/ui/ConsoleUI.java
package com.group02.ui;

import com.group02.model.Employee;
import com.group02.service.EmployeeService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Text-based UI for the Employee Management System.
 * Presents a menu loop for CRUD + bulk raise, with improved formatting.
 */
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService service;

    public ConsoleUI(EmployeeService service) {
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
                case 7 -> addDatabaseColumn();
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
        System.out.println("║ 0) Exit       7) Add field             ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private void add() {
        System.out.println("\n-- Add Employee --");
        Employee e = new Employee();
        e.setName(getString("Name: "));
        e.setSSN(getString("SSN: "));
        e.setJobTitle(getString("Job Title: "));
        e.setDivision(getString("Division: "));
        e.setSalary(getDouble("Salary: "));
        e.setPayInfo(getString("Pay Info: "));
        int id = service.add(e);
        System.out.println(id > 0 ? "Added, ID=" + id : "Add failed.");
    }

    private void addDatabaseColumn() {
        System.out.println("\n-- Add Column--");
        String columnName = getString("Column Name: ");
        String dataType = getString("Datatype: ");

        Boolean success = service.addColumnToTable(columnName, dataType, null);
        System.out.println(success ? "Added, Column=" + columnName : "Add failed.");
    }

    private void viewAll() {
        List<Employee> list = service.findAll();
        if (list.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        System.out.printf("%-5s %-20s %-10s %-15s %-10s%n",
                "ID", "Name", "SSN", "Job Title", "Salary");
        System.out.println("---------------------------------------------------------------");
        for (Employee e : list) {
            System.out.printf("%-5d %-20s %-10s %-15s $%,10.2f%n",
                    e.getEmpID(), e.getName(),
                    e.getSSN(), e.getJobTitle(),
                    e.getSalary());
        }
    }

    private void search() {
        System.out.println("\n-- Search --");
        System.out.println("1) By ID   2) By Name   3) By SSN");
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
                String ssn = getString("Enter SSN: ");
                System.out.println(service.searchBySSN(ssn).orElse(null));
            }
            default -> System.out.println("Invalid search option.");
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

        String ssn = getOpt("SSN [" + emp.getSSN() + "]: ");
        if (!ssn.isBlank())
            emp.setSSN(ssn);

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