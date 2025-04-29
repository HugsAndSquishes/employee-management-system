package com.group02.service;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeManager employeeManager;

    public EmployeeServiceImpl() {
        this.employeeManager = new EmployeeManager();
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeManager.getAll();
    }

    @Override
    public Employee getEmployeeById(int id) {
        return employeeManager.getById(id);
    }

    @Override
    public List<Employee> searchEmployees(Map<String, Object> criteria) {
        return employeeManager.search(criteria);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        return employeeManager.insert(employee);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        return employeeManager.update(employee);
    }

    @Override
    public boolean deleteEmployee(int id) {
        return employeeManager.delete(id);
    }

    @Override
    public boolean updateSalariesInRange(BigDecimal minSalary, BigDecimal maxSalary, double percentageIncrease) {
        return employeeManager.updateSalaryByRange(minSalary, maxSalary, percentageIncrease);
    }

    @Override
    public Map<String, BigDecimal> getTotalPayByJobTitle() {
        List<Employee> employees = employeeManager.getAll();
        Map<String, BigDecimal> result = new HashMap<>();

        // Group employees by job title and sum their salaries
        for (Employee employee : employees) {
            String jobTitle = employee.getJobTitle();
            BigDecimal currentTotal = result.getOrDefault(jobTitle, BigDecimal.ZERO);
            result.put(jobTitle, currentTotal.add(employee.getSalary()));
        }

        return result;
    }

    @Override
    public Map<String, BigDecimal> getTotalPayByDivision() {
        List<Employee> employees = employeeManager.getAll();
        Map<String, BigDecimal> result = new HashMap<>();

        // Group employees by division and sum their salaries
        for (Employee employee : employees) {
            String division = employee.getDivision();
            BigDecimal currentTotal = result.getOrDefault(division, BigDecimal.ZERO);
            result.put(division, currentTotal.add(employee.getSalary()));
        }

        return result;
    }
}