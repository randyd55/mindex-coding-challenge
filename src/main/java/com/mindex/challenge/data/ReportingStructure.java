package com.mindex.challenge.data;

public class ReportingStructure {
    Employee employee;

    int numberOfReports;

    public ReportingStructure(Employee e, int numberOfReports) {
        this.employee = e;
        this.numberOfReports = numberOfReports;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public Employee getEmployee() {
        return employee;
    }
}
