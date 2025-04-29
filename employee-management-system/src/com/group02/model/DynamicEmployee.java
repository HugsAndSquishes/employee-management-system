package com.group02.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A decorator class that adds dynamic field capabilities to the Employee class
 */
public class DynamicEmployee {
    private Employee baseEmployee;
    private Map<String, Object> dynamicFields;

    /**
     * Create a new DynamicEmployee wrapper around an existing Employee
     * 
     * @param employee The base employee to decorate
     */
    public DynamicEmployee(Employee employee) {
        this.baseEmployee = employee;
        this.dynamicFields = new HashMap<>();
    }

    /**
     * Get the underlying Employee object
     * 
     * @return The base Employee object
     */
    public Employee getBaseEmployee() {
        return baseEmployee;
    }

    /**
     * Add or update a dynamic field
     * 
     * @param fieldName The name of the field
     * @param value     The value to store
     */
    public void setField(String fieldName, Object value) {
        dynamicFields.put(fieldName, value);
    }

    /**
     * Get the value of a dynamic field
     * 
     * @param fieldName The name of the field to retrieve
     * @return The field value or null if not found
     */
    public Object getField(String fieldName) {
        return dynamicFields.get(fieldName);
    }

    /**
     * Check if a dynamic field exists
     * 
     * @param fieldName The field name to check
     * @return true if the field exists, false otherwise
     */
    public boolean hasField(String fieldName) {
        return dynamicFields.containsKey(fieldName);
    }

    /**
     * Remove a dynamic field
     * 
     * @param fieldName The field name to remove
     * @return The removed value or null if not present
     */
    public Object removeField(String fieldName) {
        return dynamicFields.remove(fieldName);
    }

    /**
     * Get all dynamic fields
     * 
     * @return A map of all dynamic fields
     */
    public Map<String, Object> getAllDynamicFields() {
        return new HashMap<>(dynamicFields);
    }

    /**
     * Convert to string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(baseEmployee.toString());
        sb.append("\nDynamic Fields:");
        for (Map.Entry<String, Object> entry : dynamicFields.entrySet()) {
            sb.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return sb.toString();
    }
}