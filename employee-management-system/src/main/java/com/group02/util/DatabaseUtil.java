/*
 * This class provides utility methods for database operations
 */

package com.group02.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

import com.group02.config.DatabaseConfig;

public class DatabaseUtil {

    /**
     * Gets a connection from the connection pool
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Instead of directly accessing the DataSource
            // Use a public method from DatabaseConfig that provides connections
            return com.group02.config.DatabaseConfig.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to get connection from pool: " + e.getMessage());
            throw e;
        }
    }

    /*
     * Gets a direct connection without using the connection pool
     * Useful for fallback or specific scenarios
     */
    public static Connection getSimpleConnection() {
        try {
            Properties props = new Properties();
            InputStream inputStream = DatabaseUtil.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            props.load(inputStream);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println("Failed to get simple connection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Closes database resources
     */
    public static void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (conn != null && !conn.isClosed())
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Commits a transaction if auto-commit is disabled
     */
    public static void commitTransaction(Connection conn) {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            System.err.println("Failed to commit transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Rolls back a transaction if auto-commit is disabled
     */
    public static void rollbackTransaction(Connection conn) {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Failed to rollback transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
}