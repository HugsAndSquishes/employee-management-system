// src/main/java/com/group02/ui/ConsoleUI.java
package com.group02.ui;

import com.group02.model.Employee;
import com.group02.service.EmployeeService;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Text-based UI for the Employee Management System.
 * Presents a simple menu loop for CRUD + bulk raise operations.
 */
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService service;

    /** @param service injected service implementation */
    public ConsoleUI(EmployeeService service) {
        this.service = service;
    }

    /** Main loop: display menu, read choice, dispatch action. */
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
                case 0 -> running = false;
                default -> System.out.println("Invalid option.");
            }
            System.out.println();
        }
    }

    /** Prints the available menu options. */
    private void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Add");
        System.out.println("2. View All");
        System.out.println("3. Search");
        System.out.println("4. Update");
        System.out.println("5. Delete");
        System.out.println("6. Bulk Raise");
        System.out.println("0. Exit");
    }

    /** Collects input to add a new Employee via the service. */
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

    /** Retrieves and prints all employees. */
    private void viewAll() {
        System.out.println("\n-- All Employees --");
        List<Employee> list = service.findAll();
        if (list.isEmpty()) {
            System.out.println("None.");
        } else {
            list.forEach(emp -> System.out.println(emp + "\n"));
        }
    }

    /** Offers ID / Name / SSN search, then prints result(s). */
    private void search() {
        System.out.println("\n-- Search: 1.ID 2.Name 3.SSN");
        int o = getInt("Option: ");
        switch (o) {
            case 1 -> {
                int id = getInt("ID: ");
                Optional<Employee> e = service.searchByID(id);
                System.out.println(e.orElse(null));
            }
            case 2 -> {
                String nm = getString("Name: ");
                service.searchByName(nm).forEach(emp -> System.out.println(emp + "\n"));
            }
            case 3 -> {
                String ssn = getString("SSN: ");
                System.out.println(service.searchBySSN(ssn).orElse(null));
            }
            default -> System.out.println("Invalid.");
        }
    }

    /** Prompts for empID, fetches record, then allows field-by-field edits. */
    private void update() {
        System.out.println("\n-- Update --");
        int id = getInt("ID: ");
        Optional<Employee> opt = service.searchByID(id);
        if (opt.isEmpty()) {
            System.out.println("Not found.");
            return;
        }
        Employee emp = opt.get();
        System.out.println(emp);

        // Only update if user types a non-blank input
        String nm = getOpt("Name [" + emp.getName() + "]: ");
        if (!nm.isBlank()) emp.setName(nm);

        String ssn = getOpt("SSN [" + emp.getSSN() + "]: ");
        if (!ssn.isBlank()) emp.setSSN(ssn);

        String jt = getOpt("Job [" + emp.getJobTitle() + "]: ");
        if (!jt.isBlank()) emp.setJobTitle(jt);

        String dv = getOpt("Div [" + emp.getDivision() + "]: ");
        if (!dv.isBlank()) emp.setDivision(dv);

        String sl = getOpt("Sal [" + emp.getSalary() + "]: ");
        if (!sl.isBlank()) emp.setSalary(Double.parseDouble(sl));

        String pi = getOpt("PayInfo [" + emp.getPayInfo() + "]: ");
        if (!pi.isBlank()) emp.setPayInfo(pi);

        System.out.println(service.update(emp) ? "Updated." : "Update failed.");
    }

    /** Deletes by empID via the service. */
    private void delete() {
        System.out.println("\n-- Delete --");
        int id = getInt("ID: ");
        System.out.println(service.delete(id) ? "Deleted." : "Delete failed.");
    }

    /** Applies a bulk raise over a salary range. */
    private void bulkRaise() {
        System.out.println("\n-- Bulk Raise --");
        double min = getDouble("Min Sal: ");
        double max = getDouble("Max Sal: ");
        double pct = getDouble("Pct: ");
        service.applySalaryRaise(min, max, pct);
        System.out.println("Raise applied.");
    }

    // Utility input helpers ----------------------------------------------

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
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private double getDouble(String prompt) {
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
