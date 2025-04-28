// src/main/java/com/group02/repository/Searchable.java
package com.group02.repository;

import java.util.List;
import java.util.Optional;
import com.group02.model.Employee;

/**
 * Defines read operations to find Employee records.
 */
public interface Searchable {
    /**
     * Find one employee by its unique ID.
     * @param empID the primary key
     * @return Optional containing the employee if present
     */
    Optional<Employee> searchByID(int empID);

    /**
     * Find employees whose names match the given pattern.
     * @param name regex or substring to match
     * @return List of matching Employee objects (may be empty)
     */
    List<Employee> searchByName(String name);

    /**
     * Find one employee by SSN.
     * @param ssn nine-digit SSN
     * @return Optional containing the employee if present
     */
    Optional<Employee> searchBySSN(String ssn);
}
