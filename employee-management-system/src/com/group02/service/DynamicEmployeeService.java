package com.group02.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.group02.model.DynamicEmployee;
import com.group02.model.Employee;
import com.group02.repository.DynamicEmployeeManager;
import com.group02.util.DatabaseUtil;

public class DynamicEmployeeService implements EmployeeService {
    private final DynamicEmployeeManager dynamicManager = new DynamicEmployeeManager();
    private final List<String> knownDynamicFields = new ArrayList<>();

    /**
     * Constructor that loads existing dynamic fields from the database
     */
    public DynamicEmployeeService() {
        loadExistingDynamicFields();
    }

    /**
     * Load existing dynamic fields from the database schema
     */
    private void loadExistingDynamicFields() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "employees", null);

            // Standard employee fields to skip
            Set<String> standardFields = Set.of("empID", "employeeName", "division", "jobTitle", "salary", "payInfo");

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");

                // If it's not a standard field, it's a dynamic field
                if (!standardFields.contains(columnName)) {
                    knownDynamicFields.add(columnName);
                    System.out.println("Loaded dynamic field: " + columnName);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to load dynamic fields: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add a new dynamic field to the employees table
     * 
     * @param fieldName    The name of the field to add
     * @param fieldType    The SQL type (VARCHAR(255), INT, etc.)
     * @param defaultValue The default value (can be null)
     * @return true if successful, false otherwise
     */
    public boolean addDynamicField(String fieldName, String fieldType, Object defaultValue) {
        boolean success = dynamicManager.addDynamicField(fieldName, fieldType, defaultValue);
        if (success) {
            knownDynamicFields.add(fieldName);
        }
        return success;
    }

    /**
     * Get a dynamic employee by ID
     * 
     * @param empID The employee ID
     * @return Optional containing the dynamic employee if found
     */
    public Optional<DynamicEmployee> getDynamicEmployee(int empID) {
        String[] fields = knownDynamicFields.toArray(new String[0]);
        return dynamicManager.getDynamicEmployee(empID, fields);
    }

    /**
     * Save changes to a dynamic employee
     * 
     * @param dynamicEmp The dynamic employee to save
     * @return true if successful, false otherwise
     */
    public boolean updateDynamicEmployee(DynamicEmployee dynamicEmp) {
        return dynamicManager.saveDynamicEmployee(dynamicEmp);
    }

    /**
     * Update a single dynamic field for an employee
     * 
     * @param empID     The employee ID
     * @param fieldName The field name
     * @param value     The value to set
     * @return true if successful, false otherwise
     */
    public boolean updateDynamicField(int empID, String fieldName, Object value) {
        return dynamicManager.updateDynamicField(empID, fieldName, value);
    }

    // Implementation of EmployeeService methods delegated to dynamicManager
    @Override
    public int add(Employee e) {
        return dynamicManager.addEmployee(e);
    }

    @Override
    public boolean update(Employee e) {
        return dynamicManager.updateEmployee(e);
    }

    @Override
    public boolean delete(int empID) {
        return dynamicManager.deleteEmployee(empID);
    }

    @Override
    public List<Employee> findAll() {
        return dynamicManager.findAll();
    }

    @Override
    public Optional<Employee> searchByID(int empID) {
        return dynamicManager.searchByID(empID);
    }

    @Override
    public List<Employee> searchByName(String pattern) {
        return dynamicManager.searchByName(pattern);
    }

    @Override
    public void applySalaryRaise(double minSalary, double maxSalary, double raisePercent) {
        dynamicManager.applySalaryRaise(minSalary, maxSalary, raisePercent);
    }

    @Override
    public Boolean addColumnToTable(String columnName, String columnType, Object defaultValue) {
        return addDynamicField(columnName, columnType, defaultValue);
    }
}