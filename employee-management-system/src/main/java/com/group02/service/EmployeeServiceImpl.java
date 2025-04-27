package com.group02.service;

import com.group02.model.Employee;
import com.group02.repository.EmployeeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeManager dao = new EmployeeManager();
    private final List<Employee> cache = new ArrayList<>();

    public EmployeeServiceImpl() {
        cache.addAll(dao.findAll());
    }

    @Override
    public int add(Employee e) {
        int id = dao.addEmployee(e);
        if (id > 0) cache.add(e);
        return id;
    }

    @Override
    public boolean update(Employee e) {
        boolean ok = dao.updateEmployee(e);
        if (ok) {
            for (int i = 0; i < cache.size(); i++) {
                if (cache.get(i).getEmpID() == e.getEmpID()) {
                    cache.set(i, e);
                    break;
                }
            }
        }
        return ok;
    }

    @Override
    public boolean delete(int empID) {
        boolean ok = dao.deleteEmployee(empID);
        if (ok) cache.removeIf(emp -> emp.getEmpID() == empID);
        return ok;
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public Optional<Employee> searchByID(int empID) {
        return cache.stream().filter(e -> e.getEmpID() == empID).findFirst();
    }

    @Override
    public List<Employee> searchByName(String pattern) {
        return cache.stream()
                    .filter(e -> e.getName().matches(".*" + pattern + ".*"))
                    .toList();
    }

    @Override
    public Optional<Employee> searchBySSN(String ssn) {
        return cache.stream().filter(e -> e.getSSN().equals(ssn)).findFirst();
    }

    @Override
    public void applySalaryRaise(double minSalary, double maxSalary, double raisePercent) {
        dao.applySalaryRaise(minSalary, maxSalary, raisePercent);
        for (Employee emp : cache) {
            double sal = emp.getSalary();
            if (sal >= minSalary && sal <= maxSalary) {
                emp.setSalary(Math.round(sal * (1 + raisePercent / 100) * 100) / 100.0);
            }
        }
    }
}
