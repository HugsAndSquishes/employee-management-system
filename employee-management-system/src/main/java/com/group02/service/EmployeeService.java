// src/main/java/com/group02/service/EmployeeService.java
package com.group02.service;

import com.group02.model.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Business-layer API for Employee operations.
 * Delegates to repository/DAO and maintains an in-memory cache.
 */
public interface EmployeeService {
    int add(Employee e);
    boolean update(Employee e);
    boolean delete(int empID);
    List<Employee> findAll();
    Optional<Employee> searchByID(int empID);
    List<Employee> searchByName(String pattern);
    Optional<Employee> searchBySSN(String ssn);
    void applySalaryRaise(double minSalary, double maxSalary, double raisePercent);
}
