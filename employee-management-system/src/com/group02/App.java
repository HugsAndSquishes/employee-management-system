package com.group02;

import com.group02.config.DatabaseConfig;
import com.group02.ui.ConsoleUI;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize the database with the default configuration
            DatabaseConfig.loadProperties(null);
            DatabaseConfig.initializeDatabase();

            // Start the UI
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } finally {
            // Close the database connection when done
            DatabaseConfig.closeConnection();
        }
    }
}