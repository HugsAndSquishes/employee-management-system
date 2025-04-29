// Employee.java
package com.group02.model;

import java.math.BigDecimal;

public class Employee {
    private int empID;
    private String employeeName;
    private String jobTitle;
    private String division;
    private BigDecimal salary;
    private String payInfo;

    // Constructor
    public Employee() {
    }

    public Employee(int empID, String employeeName, String jobTitle,
            String division, BigDecimal salary, String payInfo) {
        this.empID = empID;
        this.employeeName = employeeName;
        this.jobTitle = jobTitle;
        this.division = division;
        this.salary = salary;
        this.payInfo = payInfo;
    }

    // Getters and Setters
    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    @Override
    public String toString() {
        return "Employee [ID=" + empID + ", Name=" + employeeName + ", Job Title=" + jobTitle +
                ", Division=" + division + ", Salary=$" + salary + ", Pay Type=" + payInfo + "]";
    }
}