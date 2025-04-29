package com.group02.model;

public class Employee {
    private int empID;
    private String employeeName;
    private String jobTitle;
    private String division;
    private double salary;
    private String payInfo;

    public Employee() {
    }

    public Employee(String employeeName, String jobTitle, String division, double salary, String payInfo) {
        this.employeeName = employeeName;
        this.jobTitle = jobTitle;
        this.division = division;
        this.salary = salary;
        this.payInfo = payInfo;
    }

    public Employee(int empID, String employeeName) {
        this.empID = empID;
        this.employeeName = employeeName;
    }

    public String getInfo() {
        // TODO: Finish getInfo() function
        return "Placeholder";
    }

    public void raiseSalary(double percentage) {
        // Input should be actual percentage values (ex: 25) not decimals (ex: 0.25)
    }

    @Override
    public String toString() {
        return "Employee ID: " + empID + "\n" +
                "Employee Name: " + employeeName + "\n" +
                "Job Title: " + jobTitle + "\n" +
                "Division: " + division + "\n" +
                "Salary: " + salary + "\n" +
                "Pay Info: " + payInfo;
    }

    // Getters and Setters
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

    public Boolean setPayInfo(String payInfo) {
        if (payInfo.equals("FULLTIME") || payInfo.equals("PARTTIME")) {
            this.payInfo = payInfo;
            return true;
        } else {
            return false;
        }
    }
}
