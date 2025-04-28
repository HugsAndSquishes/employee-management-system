// src/main/java/com/group02/App.java
package com.group02;

import com.group02.config.DatabaseConfig;
import javafx.application.Application;
import com.group02.ui.EmployeeApp;
import com.group02.ui.ConsoleUI;
import com.group02.service.EmployeeServiceImpl;

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

        // To use the Console Interface:
        new ConsoleUI(new EmployeeServiceImpl()).run();

        // To use the JavaFX GUI:
        // Application.launch(EmployeeApp.class, args);

        DatabaseConfig.closeDataSource();
    }
}
