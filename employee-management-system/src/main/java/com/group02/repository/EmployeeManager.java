package com.group02.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.group02.model.Employee;
import com.group02.util.DatabaseUtil;

public class EmployeeManager implements Searchable, Updatable {
    public int addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (employeeName, division, SSN, jobTitle, salary, payInfo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDivision());
            stmt.setString(3, employee.getSSN());
            stmt.setString(4, employee.getJobTitle());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPayInfo());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int empID = generatedKeys.getInt(1);
                    employee.setEmpID(empID);
                    return empID;
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpID(rs.getInt("empID"));
        employee.setName(rs.getString("employeeName"));
        employee.setSSN(rs.getString("SSN"));
        employee.setJobTitle(rs.getString("jobTitle"));
        employee.setDivision(rs.getString("division"));
        employee.setSalary(rs.getDouble("salary"));
        employee.setPayInfo(rs.getString("payInfo"));
        return employee;
    }

    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    @Override
    public Optional<Employee> searchByID(int empID) {
        String sql = "SELECT * FROM employees WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> searchBySSN(String SSN) {
        String sql = "SELECT * FROM employees WHERE SSN = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, SSN);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> searchByName(String employeeName) {
        String sql = "SELECT * FROM employees WHERE employeeName REGEXP ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET employeeName = ?, division = ?, SSN = ?, jobTitle = ?, salary = ?, payInfo = ? WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getDivision());
            stmt.setString(3, employee.getSSN());
            stmt.setString(4, employee.getJobTitle());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPayInfo());
            stmt.setInt(7, employee.getEmpID());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateField(int empID, String fieldName, Object fieldValue) {
        List<String> validFields = List.of("employeeName", "division", "SSN", "jobTitle", "salary", "payInfo");
        if (!validFields.contains(fieldName)) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
        String sql = "UPDATE employees SET " + fieldName + " = ? WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (fieldValue instanceof String) stmt.setString(1, (String) fieldValue);
            else if (fieldValue instanceof Integer) stmt.setInt(1, (Integer) fieldValue);
            else if (fieldValue instanceof Double) stmt.setDouble(1, (Double) fieldValue);
            else if (fieldValue instanceof Boolean) stmt.setBoolean(1, (Boolean) fieldValue);
            else if (fieldValue == null) stmt.setNull(1, java.sql.Types.NULL);
            else stmt.setString(1, fieldValue.toString());
            stmt.setInt(2, empID);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(int empID) {
        String sql = "DELETE FROM employees WHERE empID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empID);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void applySalaryRaise(double min, double max, double rate) {
        String sql = "UPDATE employees SET salary = salary * (1 + ?/100) WHERE salary BETWEEN ? AND ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, rate);
            stmt.setDouble(2, min);
            stmt.setDouble(3, max);
            int updated = stmt.executeUpdate();
            System.out.println("Applied raise to " + updated + " employees.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
