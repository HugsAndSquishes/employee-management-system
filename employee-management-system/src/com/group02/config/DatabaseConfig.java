// DatabaseConfig.java
package com.group02.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties dbProperties;
    private static final String DEFAULT_CONFIG = "config.properties";
    private static Connection connection;
    private static boolean isTestEnvironment = false;

    // Load database properties
    public static void loadProperties(String configFile) {
        try {
            dbProperties = new Properties();
            String fileName = configFile != null ? configFile : DEFAULT_CONFIG;

            // List of possible resource paths to try
            String[] possiblePaths = {
                    fileName, // Try direct name first
                    "resources/" + fileName, // Try with resources/ prefix
                    "../resources/" + fileName, // Try one directory up
                    "src/resources/" + fileName, // Try from project root
                    "bin/resources/" + fileName // Try from bin directory
            };

            InputStream inputStream = null;
            String usedPath = null;

            // Try all possible paths
            for (String path : possiblePaths) {
                System.out.println("Trying to load properties from: " + path);

                // Try with class loader
                inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream(path);

                // If not found, try with file input stream
                if (inputStream == null) {
                    try {
                        inputStream = new java.io.FileInputStream(path);
                    } catch (java.io.FileNotFoundException e) {
                        // Just try next path
                        continue;
                    }
                }

                // If we found the file, remember the path and break the loop
                if (inputStream != null) {
                    usedPath = path;
                    break;
                }
            }

            // If we couldn't find the file, throw an exception
            if (inputStream == null) {
                System.err.println("Current working directory: " + System.getProperty("user.dir"));
                System.err.println("Classpath: " + System.getProperty("java.class.path"));
                throw new RuntimeException("Could not find " + fileName + " in any of the expected locations");
            }

            dbProperties.load(inputStream);
            inputStream.close();

            isTestEnvironment = configFile != null && configFile.contains("test");
            System.out.println("Successfully loaded database properties from: " + usedPath);
        } catch (Exception e) {
            System.err.println("Failed to load database properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize database manually
    public static void initializeDatabase() {
        String url = dbProperties.getProperty("db.url");
        String user = dbProperties.getProperty("db.user");
        String password = dbProperties.getProperty("db.password");
        String dbName = dbProperties.getProperty("db.name");

        try {
            // Load the JDBC driver
            Class.forName(dbProperties.getProperty("db.driver"));

            // Create a connection to MySQL server (without specifying database)
            Connection rootConnection = DriverManager.getConnection(url, user, password);
            Statement stmt = rootConnection.createStatement();

            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);

            // Use the database
            stmt.executeUpdate("USE " + dbName);

            // Create employees table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS employees (" +
                    "empID INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    "employeeName VARCHAR(255), " +
                    "jobTitle VARCHAR(255), " +
                    "division VARCHAR(255), " +
                    "salary DECIMAL(10,2), " +
                    "payInfo VARCHAR(8) NOT NULL, " +
                    "CONSTRAINT chk_payinfo CHECK (UPPER(payInfo) IN ('FULLTIME', 'PARTTIME'))" +
                    ")";
            stmt.executeUpdate(createTableSQL);

            // Close this initial connection
            stmt.close();
            rootConnection.close();

            // Now connect to the specific database
            connection = DriverManager.getConnection(url + "/" + dbName, user, password);
            System.out.println("Database initialized successfully: " + dbName);

        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // For testing: clear all data from the database
    public static void clearTestData() {
        if (!isTestEnvironment) {
            System.err.println("WARNING: Attempting to clear data outside test environment");
            return;
        }

        try {
            // Get a fresh connection if needed
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM employees");
            stmt.close();
            System.out.println("Test data cleared successfully");
        } catch (SQLException e) {
            System.err.println("Failed to clear test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = dbProperties.getProperty("db.url");
            String dbName = dbProperties.getProperty("db.name");
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");

            connection = DriverManager.getConnection(url + "/" + dbName, user, password);
        }
        return connection;
    }

    // This method returns a new connection (not the singleton)
    public static Connection getNewConnection() throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String dbName = dbProperties.getProperty("db.name");
        String user = dbProperties.getProperty("db.user");
        String password = dbProperties.getProperty("db.password");

        return DriverManager.getConnection(url + "/" + dbName, user, password);
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null; // Set to null after closing
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}