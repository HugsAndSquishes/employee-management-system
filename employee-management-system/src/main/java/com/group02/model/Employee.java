// src/main/java/com/group02/model/Employee.java
package com.group02.model;

/**
 * Domain object representing an employee record.
 * <p>
 * Contains identifying fields (empID, name, SSN) and
 * business data (jobTitle, division, salary, payInfo).
 * </p>
 */
public class Employee {
    private int empID;
    private String employeeName;
    private String SSN;
    private String jobTitle;
    private String division;
    private double salary;
    private String payInfo;

    /** No-arg constructor for frameworks or manual population. */
    public Employee() { }

    /**
     * Full constructor for creating a new Employee (before empID is assigned).
     * @param employeeName full name
     * @param SSN nine-digit SSN (no dashes)
     * @param jobTitle role/title
     * @param division department
     * @param salary annual salary
     * @param payInfo any additional pay-type info
     */
    public Employee(String employeeName, String SSN, String jobTitle,
                    String division, double salary, String payInfo) {
        this.employeeName = employeeName;
        this.SSN = SSN;
        this.jobTitle = jobTitle;
        this.division = division;
        this.salary = salary;
        this.payInfo = payInfo;
    }

    /**
     * Partial constructor used for lightweight lookups.
     * @param empID generated ID
     * @param employeeName name
     * @param SSN SSN
     */
    public Employee(int empID, String employeeName, String SSN) {
        this.empID = empID;
        this.employeeName = employeeName;
        this.SSN = SSN;
    }

    /**
     * Returns a human-readable multi-line summary.
     */
    @Override
    public String toString() {
        return "Employee ID: " + empID + "\n" +
               "Name: " + employeeName + "\n" +
               "SSN: " + SSN + "\n" +
               "Job Title: " + jobTitle + "\n" +
               "Division: " + division + "\n" +
               "Salary: " + salary + "\n" +
               "Pay Info: " + payInfo;
    }

    // Getters & setters -------------------------------------------------------

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getName() {
        return employeeName;
    }

    public void setName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getSSN() {
        return SSN;
    }

    public void setSSN(String SSN) {
        this.SSN = SSN;
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }
}
