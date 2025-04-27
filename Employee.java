public class Employee {
    private int empid;
    private String name;
    private String ssn; 
    private String jobTitle;
    private String division;
    private double salary;
    private String payStatementHistory;

    public Employee() {} 

    public Employee(int empid, String name, String ssn, String jobTitle,
                    String division, double salary, String payStatementHistory) {
        this.empid = empid;
        this.name = name;
        this.ssn = ssn;
        this.jobTitle = jobTitle;
        this.division = division;
        this.salary = salary;
        this.payStatementHistory = payStatementHistory;
    }

    public int getEmpid() {
        return empid;
    }

    public void setEmpid(int empid) {
        this.empid = empid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
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

    public String getPayStatementHistory() {
        return payStatementHistory;
    }

    public void setPayStatementHistory(String payStatementHistory) {
        this.payStatementHistory = payStatementHistory;
    }
}

