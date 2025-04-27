package com.group02.model;

public class Employee {
    private int empID;
    private String name;
    private String SSN;
    private String jobTitle;
    private String division;
    private double salary;
    private String payInfo;

    public Employee() {
    }

    public Employee(String name, String SSN, String jobTitle, String division, double salary, String payInfo) {
        this.name = name;
        this.SSN = SSN;
        this.jobTitle = jobTitle;
        this.division = division;
        this.salary = salary;
        this.payInfo = payInfo;
    }

    public Employee(int empID, String name, String SSN) {
        this.empID = empID;
        this.name = name;
        this.SSN = SSN;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Employee{id=" + name + ", Title='" + jobTitle + "', Division='" + division + "'}";
    }
}
