package com.group02.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.group02.model.DynamicEmployee;
import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import com.group02.util.DatabaseUtil;

/**
 * Extends EmployeeManager with dynamic field capabilities
 */
public class DynamicEmployeeManager extends EmployeeManager {

    /**
     * Add a dynamic field to the employees table and update schema
     * 
     * @param fieldName    The name of the field to add
     * @param fieldType    The SQL data type (e.g., VARCHAR(255), INT)
     * @param defaultValue Default value (can be null)
     * @return true if successful, false otherwise
     */
    public boolean addDynamicField(String fieldName, String fieldType, Object defaultValue) {
        // First add the column to the table
        return super.addColumnToTable(fieldName, fieldType, defaultValue);
    }

    /**
     * Update a dynamic field value for a specific employee
     * 
     * @param empID     The employee ID
     * @param fieldName The dynamic field name
     * @param value     The value to set
     * @return true if successful, false otherwise
     */
    public boolean updateDynamicField(int empID, String fieldName, Object value) {
        // This leverages the existing updateField method from EmployeeManager
        return super.updateField(empID, fieldName, value);
    }

    /**
     * Get all dynamic fields for an employee
     * 
     * @param empID       The employee ID
     * @param knownFields List of dynamic field names to retrieve
     * @return Map of field names to values
     */
    public Map<String, Object> getDynamicFields(int empID, String[] knownFields) {
        Map<String, Object> fields = new HashMap<>();

        if (knownFields.length == 0) {
            return fields;
        }

        // Build a query to get all dynamic fields at once
        StringBuilder query = new StringBuilder("SELECT ");
        for (int i = 0; i < knownFields.length; i++) {
            if (i > 0) {
                query.append(", ");
            }
            query.append(knownFields[i]);
        }
        // Make sure we're using the correct column name from the database
        query.append(" FROM employees WHERE empID = ?"); // empID not employee_id

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            stmt.setInt(1, empID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    for (String field : knownFields) {
                        // Result might be any type, so get as Object
                        Object value = rs.getObject(field);
                        if (!rs.wasNull()) {
                            fields.put(field, value);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Query was: " + query.toString()); // Add this for debugging
        }

        return fields;
    }

    /**
     * Creates a DynamicEmployee by wrapping an Employee with its dynamic fields
     * 
     * @param empID       The employee ID
     * @param knownFields Array of dynamic field names
     * @return Optional containing the DynamicEmployee if found
     */
    public Optional<DynamicEmployee> getDynamicEmployee(int empID, String[] knownFields) {
        // First get the base employee
        Optional<Employee> empOpt = super.searchByID(empID);

        if (empOpt.isEmpty()) {
            return Optional.empty();
        }

        // Create the dynamic employee wrapper
        DynamicEmployee dynamicEmp = new DynamicEmployee(empOpt.get());

        // Populate with dynamic fields
        Map<String, Object> fields = getDynamicFields(empID, knownFields);
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            dynamicEmp.setField(entry.getKey(), entry.getValue());
        }

        return Optional.of(dynamicEmp);
    }

    /**
     * Save a dynamic employee to the database
     * 
     * @param dynamicEmp The dynamic employee to save
     * @return true if successful, false otherwise
     */
    public boolean saveDynamicEmployee(DynamicEmployee dynamicEmp) {
        Employee baseEmp = dynamicEmp.getBaseEmployee();
        int empID = baseEmp.getEmpID();

        // First update the base employee
        boolean success = super.updateEmployee(baseEmp);
        if (!success) {
            return false;
        }

        // Then update each dynamic field
        Map<String, Object> fields = dynamicEmp.getAllDynamicFields();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            success = super.updateField(empID, entry.getKey(), entry.getValue());
            if (!success) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if any employee has a specific field value
     * 
     * @param fieldName  The name of the field to search
     * @param fieldValue The value to search for
     * @return true if at least one employee has this field value, false otherwise
     */
    public boolean fieldValueExists(String fieldName, Object fieldValue) {
        String sql = "SELECT COUNT(*) FROM employees WHERE " + fieldName + " = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // bind based on runtime type
            if (fieldValue instanceof String)
                stmt.setString(1, (String) fieldValue);
            else if (fieldValue instanceof Integer)
                stmt.setInt(1, (Integer) fieldValue);
            else if (fieldValue instanceof Double)
                stmt.setDouble(1, (Double) fieldValue);
            else if (fieldValue instanceof Boolean)
                stmt.setBoolean(1, (Boolean) fieldValue);
            else if (fieldValue == null)
                stmt.setNull(1, Types.VARCHAR);
            else
                stmt.setString(1, fieldValue.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}