package com.mindex.challenge.data;

public class ReportingStructure {
    private final Employee e;

    private final int numberOfReports;

    public ReportingStructure(Employee e, int numberOfReports) {
        this.e = e;
        this.numberOfReports = numberOfReports;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public Employee getEmployee() {
        return e;
    }
}
