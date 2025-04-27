package com.group02;

import com.group02.config.DatabaseConfig;
import com.group02.service.EmployeeService;
import com.group02.service.EmployeeServiceImpl;
import com.group02.ui.ConsoleUI;

public class App {
    public static void main(String[] args) {
        try {
            DatabaseConfig.loadProperties(null);
            DatabaseConfig.initializeDatabaseSchema();
            DatabaseConfig.initializeConnectionPool();
        } catch (Exception e) {
            System.err.println("Initialization failed: " + e.getMessage());
            System.exit(1);
        }

        EmployeeService service = new EmployeeServiceImpl();
        new ConsoleUI(service).run();

        DatabaseConfig.closeDataSource();
    }
}
