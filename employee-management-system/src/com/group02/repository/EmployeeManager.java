package com.group02.repository;

import com.group02.config.DatabaseConfig;
import com.group02.model.Employee;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeManager implements Searchable<Employee>, Updatable<Employee> {

    @Override
    public List<Employee> search(Map<String, Object> criteria) {
        List<Employee> employees = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM employees WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        // Add search criteria to query
        if (criteria.containsKey("empID")) {
            queryBuilder.append(" AND empID = ?");
            parameters.add(criteria.get("empID"));
        }

        if (criteria.containsKey("employeeName")) {
            queryBuilder.append(" AND employeeName LIKE ?");
            parameters.add("%" + criteria.get("employeeName") + "%");
        }

        if (criteria.containsKey("jobTitle")) {
            queryBuilder.append(" AND jobTitle = ?");
            parameters.add(criteria.get("jobTitle"));
        }

        if (criteria.containsKey("division")) {
            queryBuilder.append(" AND division = ?");
            parameters.add(criteria.get("division"));
        }

        if (criteria.containsKey("payInfo")) {
            queryBuilder.append(" AND payInfo = ?");
            parameters.add(criteria.get("payInfo"));
        }

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee(
                            rs.getInt("empID"),
                            rs.getString("employeeName"),
                            rs.getString("jobTitle"),
                            rs.getString("division"),
                            rs.getBigDecimal("salary"),
                            rs.getString("payInfo"));
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public boolean update(Employee employee) {
        String sql = "UPDATE employees SET employeeName = ?, jobTitle = ?, division = ?, " +
                "salary = ?, payInfo = ? WHERE empID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getJobTitle());
            stmt.setString(3, employee.getDivision());
            stmt.setBigDecimal(4, employee.getSalary());
            stmt.setString(5, employee.getPayInfo());
            stmt.setInt(6, employee.getEmpID());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean insert(Employee employee) {
        String sql = "INSERT INTO employees (employeeName, jobTitle, division, salary, payInfo) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getJobTitle());
            stmt.setString(3, employee.getDivision());
            stmt.setBigDecimal(4, employee.getSalary());
            stmt.setString(5, employee.getPayInfo());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmpID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error inserting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int empID) {
        String sql = "DELETE FROM employees WHERE empID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSalaryByRange(BigDecimal minSalary, BigDecimal maxSalary, double percentageIncrease) {
        String sql = "UPDATE employees SET salary = salary * (1 + ?) " +
                "WHERE salary >= ? AND salary < ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, percentageIncrease / 100.0);
            stmt.setBigDecimal(2, minSalary);
            stmt.setBigDecimal(3, maxSalary);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating salaries: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("empID"),
                        rs.getString("employeeName"),
                        rs.getString("jobTitle"),
                        rs.getString("division"),
                        rs.getBigDecimal("salary"),
                        rs.getString("payInfo"));
                employees.add(employee);
            }

        } catch (SQLException e) {
            System.err.println("Error getting all employees: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public Employee getById(int empID) {
        String sql = "SELECT * FROM employees WHERE empID = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getInt("empID"),
                            rs.getString("employeeName"),
                            rs.getString("jobTitle"),
                            rs.getString("division"),
                            rs.getBigDecimal("salary"),
                            rs.getString("payInfo"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting employee by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}