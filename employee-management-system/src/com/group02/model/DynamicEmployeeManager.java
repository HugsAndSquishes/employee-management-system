package com.group02.repository;

import com.group02.config.DatabaseConfig;
import com.group02.model.DynamicEmployee;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DynamicEmployeeManager extends EmployeeManager {

    // Method to add a column to the employee table
    public boolean addColumn(String columnName, String dataType) {
        String sql = "ALTER TABLE employees ADD COLUMN " + columnName + " " + dataType;

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Column '" + columnName + "' added successfully");
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding column: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to get table metadata
    public Map<String, String> getTableColumns() {
        Map<String, String> columns = new HashMap<>();

        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, "employees", null);

            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME");
                columns.put(columnName, dataType);
            }

        } catch (SQLException e) {
            System.err.println("Error getting table metadata: " + e.getMessage());
            e.printStackTrace();
        }

        return columns;
    }

    // Method to get a DynamicEmployee with all columns
    public DynamicEmployee getDynamicEmployeeById(int empID) {
        Map<String, String> columns = getTableColumns();

        // Build column list dynamically based on actual table columns
        StringBuilder columnList = new StringBuilder();
        boolean first = true;

        for (String column : columns.keySet()) {
            if (!first) {
                columnList.append(", ");
            }
            columnList.append(column);
            first = false;
        }

        String sql = "SELECT " + columnList.toString() + " FROM employees WHERE empID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DynamicEmployee employee = new DynamicEmployee(
                            rs.getInt("empID"),
                            rs.getString("employeeName"),
                            rs.getString("jobTitle"),
                            rs.getString("division"),
                            rs.getBigDecimal("salary"),
                            rs.getString("payInfo"));

                    // Add dynamic attributes (excluding base attributes)
                    for (String column : columns.keySet()) {
                        // Skip base columns
                        if (column.equals("empID") || column.equals("employeeName") ||
                                column.equals("jobTitle") || column.equals("division") ||
                                column.equals("salary") || column.equals("payInfo")) {
                            continue;
                        }

                        employee.addAttribute(column, rs.getObject(column));
                    }

                    return employee;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting dynamic employee: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Update a dynamic employee with its dynamic attributes
    public boolean updateDynamicEmployee(DynamicEmployee employee) {
        // First update base employee fields
        boolean baseResult = super.update(employee);

        if (!baseResult) {
            return false;
        }

        // Then update dynamic fields
        Map<String, Object> dynamicAttributes = employee.getAllDynamicAttributes();
        if (dynamicAttributes.isEmpty()) {
            return true; // No dynamic attributes to update
        }

        StringBuilder sql = new StringBuilder("UPDATE employees SET ");

        int i = 0;
        for (String attribute : dynamicAttributes.keySet()) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(attribute).append(" = ?");
            i++;
        }

        sql.append(" WHERE empID = ?");

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            i = 1;
            for (Object value : dynamicAttributes.values()) {
                stmt.setObject(i++, value);
            }

            stmt.setInt(i, employee.getEmpID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating dynamic employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}