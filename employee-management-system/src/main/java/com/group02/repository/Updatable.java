// src/main/java/com/group02/repository/Updatable.java
package com.group02.repository;

/**
 * Defines a single-field update operation for Employee records.
 */
public interface Updatable {
    /**
     * Update one column on the specified employee.
     * @param empID primary key of the employee
     * @param fieldName the column name to update
     * @param fieldValue new value for the column
     * @return true if update succeeded, false otherwise
     */
    boolean updateField(int empID, String fieldName, Object fieldValue);
}
