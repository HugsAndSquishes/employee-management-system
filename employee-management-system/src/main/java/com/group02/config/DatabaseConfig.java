/*
This class handles the configuration and initialization of database connections
 */
package com.group02.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static Properties dbProperties;

    // Load database properties
    static {
        try {
            dbProperties = new Properties();
            InputStream inputStream = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            dbProperties.load(inputStream);
        } catch (Exception e) {
            System.err.println("Failed to load database properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize the database connection pool
    public static void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbProperties.getProperty("db.url"));
            config.setUsername(dbProperties.getProperty("db.user"));
            config.setPassword(dbProperties.getProperty("db.password"));
            config.setDriverClassName(dbProperties.getProperty("db.driver"));

            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);

            dataSource = new HikariDataSource(config);
            System.out.println("Database connection pool initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize Flyway migrations
    public static void initializeDatabaseSchema() {
        try {
            String url = dbProperties.getProperty("db.url");
            String user = dbProperties.getProperty("db.user");
            String password = dbProperties.getProperty("db.password");
            // Extract schema name from JDBC URL
            String schema = url.substring(url.lastIndexOf("/") + 1);
            if (schema.contains("?")) {
                schema = schema.substring(0, schema.indexOf("?"));
            }

            // Use root URL without schema for initial connection
            String rootUrl = url.substring(0, url.lastIndexOf("/"));

            Flyway flyway = Flyway.configure()
                    .dataSource(rootUrl, user, password)
                    .schemas(schema)
                    .createSchemas(true)
                    .baselineOnMigrate(true)
                    .locations("classpath:db/migration")
                    .load();

            MigrateResult result = flyway.migrate();
            int migrationsApplied = result.migrationsExecuted;
            System.out.println("Applied " + migrationsApplied + " database migrations");
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
}
