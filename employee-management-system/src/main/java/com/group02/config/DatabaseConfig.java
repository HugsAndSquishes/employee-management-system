/*
This class handles the configuration and initialization of database connections
 */
package com.group02.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static Properties dbProperties;
    private static final String DEFAULT_CONFIG = "config.properties";

    // Load database properties
    private static boolean propertiesLoaded = false;

    public static void loadProperties(String configFile) {
        try {
            if (propertiesLoaded && configFile == null) {
                return; // Already loaded with default properties
            }

            dbProperties = new Properties();
            String fileToLoad = configFile != null ? configFile : DEFAULT_CONFIG;
            InputStream inputStream = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream(fileToLoad);
            if (inputStream == null) {
                throw new RuntimeException("Could not find " + fileToLoad);
            }
            dbProperties.load(inputStream);
            propertiesLoaded = true;
            System.out.println("Loaded database properties from: " + fileToLoad);
        } catch (Exception e) {
            System.err.println("Failed to load database properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getUrl() {
        // Ensure database name is included in URL
        String baseUrl = dbProperties.getProperty("db.url");
        String dbName = dbProperties.getProperty("db.name");

        // If URL already has a database name, return as is
        if (baseUrl.indexOf("/", baseUrl.lastIndexOf(":") + 1) > 0) {
            return baseUrl;
        }

        // Otherwise, append the database name
        return baseUrl + "/" + dbName;
    }

    // Initialize the database connection pool with configurable properties
    public static void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(getUrl());
            config.setUsername(dbProperties.getProperty("db.user"));
            config.setPassword(dbProperties.getProperty("db.password"));
            config.setDriverClassName(dbProperties.getProperty("db.driver"));

            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);

            dataSource = new HikariDataSource(config);
            System.out.println("Database connection pool initialized successfully for: " +
                    dbProperties.getProperty("db.name"));
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize Flyway migrations with configurable schema name
    public static void initializeDatabaseSchema() {
        try {
            String url = getUrl();
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");
            String dbName = dbProperties.getProperty("db.name");

            // Use root URL without schema for initial connection
            String rootUrl = url.substring(0, url.lastIndexOf("/"));

            Flyway flyway = Flyway.configure()
                    .dataSource(rootUrl, user, password)
                    .schemas(dbName)
                    .createSchemas(true)
                    .baselineOnMigrate(true)
                    .locations("classpath:db/migration")
                    .placeholders(Map.of("database_name", dbName))
                    .load();

            MigrateResult result = flyway.migrate();
            int migrationsApplied = result.migrationsExecuted;
            System.out.println("Applied " + migrationsApplied + " database migrations to schema: " + dbName);
        } catch (Exception e) {
            System.err.println("Database schema initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Get the datasource for use in DatabaseUtil
    protected static HikariDataSource getDataSource() {
        return dataSource;
    }

    // Close the datasource when application shuts down
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is not initialized");
        }
        return dataSource.getConnection();
    }

    public static void resetConfiguration() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = null;
        dbProperties = null;
        propertiesLoaded = false;
    }
}
