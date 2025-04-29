package com.group02;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import com.group02.service.DynamicEmployeeServiceImpl;
import com.group02.ui.ConsoleUI;
import com.group02.config.DatabaseConfig;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);

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

        // Create our dynamic service implementation instead of standard one
        DynamicEmployeeServiceImpl dynamicService = new DynamicEmployeeServiceImpl();

        // Pass the dynamic service to the ConsoleUI
        new ConsoleUI(dynamicService).run();

        DatabaseConfig.closeConnection();
    }
}