/**
 * Interface that contains search operations for employees
 */

package com.group02.repository;

import java.util.List;
import java.util.Optional;
import com.group02.model.Employee;

public interface Searchable {
    /**
     * Search for an employee by their empID
     * 
     * @param empID The employee ID to search for
     * @return Optional containing the employee if found
     */
    Optional<Employee> searchByID(int empID);

    /**
     * Search for employees by their name
     * 
     * @param name The employee name to search for
     * @return List of employees that match the provided name. Note that this is a
     *         List object because multiple employees can have the same name
     */
    List<Employee> searchByName(String name);

    /**
     * Search for an employee by their SSN
     * 
     * @param ssn The SSN to search for
     * @return Optional containing the employee if found
     */
    Optional<Employee> searchBySSN(String ssn);
}
