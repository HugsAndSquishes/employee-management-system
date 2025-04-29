package com.group02.service;

import com.group02.model.Employee;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    Employee getEmployeeById(int id);

    List<Employee> searchEmployees(Map<String, Object> criteria);

    boolean addEmployee(Employee employee);

    boolean updateEmployee(Employee employee);

    boolean deleteEmployee(int id);

    boolean updateSalariesInRange(BigDecimal minSalary, BigDecimal maxSalary, double percentageIncrease);

    Map<String, BigDecimal> getTotalPayByJobTitle();

    Map<String, BigDecimal> getTotalPayByDivision();
}