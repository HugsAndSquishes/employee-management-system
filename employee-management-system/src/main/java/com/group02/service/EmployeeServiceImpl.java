// src/main/java/com/group02/service/EmployeeServiceImpl.java
package com.group02.service;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EmployeeService.
 * <p>
 * Fetches initial data into an in-memory List cache, then
 * delegates all writes to EmployeeManager. Updates cache on success.
 * </p>
 */
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeManager dao = new EmployeeManager();
    private final List<Employee> cache = new ArrayList<>();

    /** Load initial cache from DB on startup. */
    public EmployeeServiceImpl() {
        cache.addAll(dao.findAll());
    }

    /** {@inheritDoc} */
    @Override
    public int add(Employee e) {
        int id = dao.addEmployee(e);
        if (id > 0) {
            cache.add(e);
        }
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public boolean update(Employee e) {
        boolean ok = dao.updateEmployee(e);
        if (ok) {
            // sync cache
            for (int i = 0; i < cache.size(); i++) {
                if (cache.get(i).getEmpID() == e.getEmpID()) {
                    cache.set(i, e);
                    break;
                }
            }
        }
        return ok;
    }

    /** {@inheritDoc} */
    @Override
    public boolean delete(int empID) {
        boolean ok = dao.deleteEmployee(empID);
        if (ok) {
            cache.removeIf(emp -> emp.getEmpID() == empID);
        }
        return ok;
    }

    /** {@inheritDoc} */
    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(cache);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Employee> searchByID(int empID) {
        return cache.stream()
                    .filter(e -> e.getEmpID() == empID)
                    .findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public List<Employee> searchByName(String pattern) {
        // regex match in memory
        return cache.stream()
                    .filter(e -> e.getName().matches(".*" + pattern + ".*"))
                    .toList();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Employee> searchBySSN(String ssn) {
        return cache.stream()
                    .filter(e -> e.getSSN().equals(ssn))
                    .findFirst();
    }

    /** {@inheritDoc} */
    @Override
    public void applySalaryRaise(double minSalary, double maxSalary, double raisePercent) {
        dao.applySalaryRaise(minSalary, maxSalary, raisePercent);
        // update cached objects as well
        for (Employee emp : cache) {
            double sal = emp.getSalary();
            if (sal >= minSalary && sal <= maxSalary) {
                // round to 2 decimals
                emp.setSalary(Math.round(sal * (1 + raisePercent / 100) * 100) / 100.0);
            }
        }
    }
}
