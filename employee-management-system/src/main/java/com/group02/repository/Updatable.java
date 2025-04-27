/**
 * Interface that contains updated operations for employees
 */

package com.group02.repository;

public interface Updatable {
    /**
     * Updates a single field in the employee database
     * 
     * @param empID      The employee ID
     * @param fieldName  The name of the field to update
     * @param fieldValue The new value for the field
     * @return true if update successful, false otherwise
     */
    boolean updateField(int empID, String fieldName, Object fieldValue);

}
