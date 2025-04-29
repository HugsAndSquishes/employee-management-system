package com.group02.service;

import com.group02.model.DynamicEmployee;
import com.group02.model.Employee;
import com.group02.repository.DynamicEmployeeManager;
import java.sql.DatabaseMetaData;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.group02.util.DatabaseUtil;

/**
 * Implementation of EmployeeService that supports dynamic fields with caching.
 * <p>
 * Extends the functionality of EmployeeServiceImpl to handle dynamic fields
 * while maintaining the in-memory caching performance benefits.
 * </p>
 */
public class DynamicEmployeeServiceImpl extends EmployeeServiceImpl {
    private final DynamicEmployeeManager dynamicDao;
    private final List<String> knownDynamicFields = new ArrayList<>();
    private final Map<Integer, Map<String, Object>> dynamicFieldsCache = new HashMap<>();
    private final Map<String, Integer> fieldTypesMap = new HashMap<>();

    /**
     * Create a new service with dynamic field support
     */
    public DynamicEmployeeServiceImpl() {
        super(); // Initialize the base cache
        this.dynamicDao = new DynamicEmployeeManager();

        // Load existing dynamic fields from database schema
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
                int dataType = columns.getInt("DATA_TYPE");

                // If it's not a standard field, it's a dynamic field
                if (!standardFields.contains(columnName)) {
                    knownDynamicFields.add(columnName);
                    fieldTypesMap.put(columnName, dataType);
                    System.out.println("Loaded dynamic field: " + columnName);
                }
            }
        } catch (SQLException e) {
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
        boolean success = dynamicDao.addDynamicField(fieldName, fieldType, defaultValue);
        if (success) {
            knownDynamicFields.add(fieldName);

            // Store the SQL type
            int sqlType = Types.VARCHAR; // Default
            String upperType = fieldType.toUpperCase();

            if (upperType.contains("INT")) {
                sqlType = Types.INTEGER;
            } else if (upperType.contains("FLOAT") || upperType.contains("REAL")) {
                sqlType = Types.FLOAT;
            } else if (upperType.contains("DOUBLE") || upperType.contains("DECIMAL")) {
                sqlType = Types.DOUBLE;
            } else if (upperType.contains("DATE")) {
                sqlType = Types.DATE;
            } else if (upperType.contains("TIME")) {
                sqlType = Types.TIME;
            } else if (upperType.contains("BOOLEAN")) {
                sqlType = Types.BOOLEAN;
            } else if (upperType.contains("CHAR") || upperType.contains("TEXT") || upperType.contains("VARCHAR")) {
                sqlType = Types.VARCHAR;
            }

            fieldTypesMap.put(fieldName, sqlType);

            // Update cache with default values (if provided)
            if (defaultValue != null) {
                List<Employee> allEmployees = super.findAll();
                for (Employee emp : allEmployees) {
                    int empID = emp.getEmpID();
                    Map<String, Object> empFields = dynamicFieldsCache.computeIfAbsent(empID, k -> new HashMap<>());
                    empFields.put(fieldName, defaultValue);
                }
            }
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
        Optional<Employee> empOpt = super.searchByID(empID);
        if (empOpt.isEmpty()) {
            return Optional.empty();
        }

        DynamicEmployee dynamicEmp = new DynamicEmployee(empOpt.get());

        // Check cache first
        Map<String, Object> cachedFields = dynamicFieldsCache.get(empID);

        // If not in cache or incomplete, fetch from database
        if (cachedFields == null || cachedFields.size() < knownDynamicFields.size()) {
            String[] fieldNames = knownDynamicFields.toArray(new String[0]);
            Map<String, Object> dbFields = dynamicDao.getDynamicFields(empID, fieldNames);

            // Initialize cache entry if needed
            if (cachedFields == null) {
                cachedFields = new HashMap<>();
                dynamicFieldsCache.put(empID, cachedFields);
            }

            // Update cache with database values
            cachedFields.putAll(dbFields);
        }

        // Populate dynamic employee with cached values
        for (Map.Entry<String, Object> entry : cachedFields.entrySet()) {
            dynamicEmp.setField(entry.getKey(), entry.getValue());
        }

        return Optional.of(dynamicEmp);
    }

    /**
     * Get all employees as dynamic employees
     * 
     * @return List of dynamic employees
     */
    public List<DynamicEmployee> findAllDynamicEmployees() {
        List<Employee> baseEmployees = super.findAll();
        List<DynamicEmployee> dynamicEmployees = new ArrayList<>();

        for (Employee emp : baseEmployees) {
            getDynamicEmployee(emp.getEmpID()).ifPresent(dynamicEmployees::add);
        }

        return dynamicEmployees;
    }

    /**
     * Update a dynamic employee
     * 
     * @param dynamicEmp The dynamic employee to update
     * @return true if successful, false otherwise
     */
    public boolean updateDynamicEmployee(DynamicEmployee dynamicEmp) {
        Employee baseEmp = dynamicEmp.getBaseEmployee();
        int empID = baseEmp.getEmpID();

        // First update base employee (this will update the base cache too)
        boolean success = super.update(baseEmp);
        if (!success) {
            return false;
        }

        // Update dynamic fields in database
        Map<String, Object> fields = dynamicEmp.getAllDynamicFields();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            success = dynamicDao.updateDynamicField(empID, fieldName, value);
            if (!success) {
                return false;
            }

            // Update cache
            Map<String, Object> cachedFields = dynamicFieldsCache.computeIfAbsent(empID, k -> new HashMap<>());
            cachedFields.put(fieldName, value);
        }

        return true;
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
        boolean success = dynamicDao.updateDynamicField(empID, fieldName, value);

        if (success) {
            // Update cache
            Map<String, Object> cachedFields = dynamicFieldsCache.computeIfAbsent(empID, k -> new HashMap<>());
            cachedFields.put(fieldName, value);
        }

        return success;
    }

    /**
     * Add a new employee with dynamic fields
     * 
     * @param dynamicEmp The dynamic employee to add
     * @return The generated employee ID or -1 if failed
     */
    public int addDynamicEmployee(DynamicEmployee dynamicEmp) {
        Employee baseEmp = dynamicEmp.getBaseEmployee();

        // Add the base employee first (this will update the base cache too)
        int empID = super.add(baseEmp);
        if (empID <= 0) {
            return -1;
        }

        // Update dynamic fields
        Map<String, Object> fields = dynamicEmp.getAllDynamicFields();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            boolean success = dynamicDao.updateDynamicField(empID, fieldName, value);
            if (!success) {
                return -1;
            }
        }

        // Update cache
        Map<String, Object> cachedFields = new HashMap<>();
        cachedFields.putAll(fields);
        dynamicFieldsCache.put(empID, cachedFields);

        return empID;
    }

    /**
     * Delete an employee and their dynamic fields
     * 
     * @param empID The employee ID to delete
     * @return true if successful, false otherwise
     */
    @Override
    public boolean delete(int empID) {
        boolean success = super.delete(empID);
        if (success) {
            // Remove from dynamic fields cache
            dynamicFieldsCache.remove(empID);
        }
        return success;
    }

    /**
     * Add a column to the database table
     * This is an alias for addDynamicField for interface compatibility
     */
    @Override
    public Boolean addColumnToTable(String columnName, String columnType, Object defaultValue) {
        return addDynamicField(columnName, columnType, defaultValue);
    }

    /**
     * Get the list of known dynamic field names
     * 
     * @return List of field names
     */
    public List<String> getKnownDynamicFields() {
        return new ArrayList<>(knownDynamicFields);
    }

    /**
     * Search for employees by dynamic field value
     * 
     * @param fieldName  The name of the dynamic field to search
     * @param fieldValue The value to search for
     * @return List of employees matching the search criteria
     */
    public List<DynamicEmployee> searchByDynamicField(String fieldName, Object fieldValue) {
        List<DynamicEmployee> results = new ArrayList<>();

        // Check if this is a known dynamic field
        if (!knownDynamicFields.contains(fieldName)) {
            return results; // Empty list if field doesn't exist
        }

        // First, check our dynamic field cache
        for (Map.Entry<Integer, Map<String, Object>> entry : dynamicFieldsCache.entrySet()) {
            int empID = entry.getKey();
            Map<String, Object> fields = entry.getValue();

            // Check if this employee has the field with matching value
            if (fields.containsKey(fieldName)) {
                Object cachedValue = fields.get(fieldName);

                boolean matches = false;
                // Compare based on object type
                if (fieldValue == null) {
                    matches = (cachedValue == null);
                } else if (cachedValue != null) {
                    // For strings, do case-insensitive contains check
                    if (fieldValue instanceof String && cachedValue instanceof String) {
                        matches = ((String) cachedValue).toLowerCase()
                                .contains(((String) fieldValue).toLowerCase());
                    } else {
                        // For other types, check equality
                        matches = cachedValue.equals(fieldValue);
                    }
                }

                if (matches) {
                    // Get the full dynamic employee and add to results
                    getDynamicEmployee(empID).ifPresent(results::add);
                }
            }
        }

        // If our cache doesn't have complete data, search the database
        if (dynamicFieldsCache.size() < super.findAll().size()) {
            // Build SQL query to search by field value
            String sql = "SELECT empID FROM employees WHERE " + fieldName + " = ?";

            try (Connection conn = DatabaseUtil.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Set the parameter based on type
                if (fieldValue instanceof String) {
                    stmt.setString(1, (String) fieldValue);
                } else if (fieldValue instanceof Integer) {
                    stmt.setInt(1, (Integer) fieldValue);
                } else if (fieldValue instanceof Double) {
                    stmt.setDouble(1, (Double) fieldValue);
                } else if (fieldValue instanceof Boolean) {
                    stmt.setBoolean(1, (Boolean) fieldValue);
                } else if (fieldValue == null) {
                    // Get the appropriate SQL type for this field
                    int sqlType = fieldTypesMap.getOrDefault(fieldName, Types.VARCHAR);
                    stmt.setNull(1, sqlType);
                } else {
                    stmt.setString(1, fieldValue.toString());
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int empID = rs.getInt("empID");

                        // Check if we already found this employee in cache
                        boolean alreadyFound = results.stream()
                                .anyMatch(emp -> emp.getBaseEmployee().getEmpID() == empID);

                        if (!alreadyFound) {
                            getDynamicEmployee(empID).ifPresent(results::add);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * More flexible search that allows pattern matching for string fields
     * 
     * @param fieldName The name of the dynamic field to search
     * @param pattern   The pattern to search for (uses SQL LIKE syntax)
     * @return List of employees matching the search criteria
     */
    public List<DynamicEmployee> searchByDynamicFieldPattern(String fieldName, String pattern) {
        List<DynamicEmployee> results = new ArrayList<>();

        // Check if this is a known dynamic field
        if (!knownDynamicFields.contains(fieldName)) {
            return results; // Empty list if field doesn't exist
        }

        // This search is better handled directly at the database
        String sql = "SELECT empID FROM employees WHERE " + fieldName + " LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + pattern + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int empID = rs.getInt("empID");
                    getDynamicEmployee(empID).ifPresent(results::add);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}