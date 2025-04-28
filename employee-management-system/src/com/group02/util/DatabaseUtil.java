/*
 * This class provides utility methods for database operations
 */

package com.group02.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.group02.config.DatabaseConfig;

public class DatabaseUtil {
    /**
     * Gets a connection from the DatabaseConfig
     */
    public static Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
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

        // NOTE: We don't close the main connection here anymore
        // since we're managing a single connection in DatabaseConfig
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