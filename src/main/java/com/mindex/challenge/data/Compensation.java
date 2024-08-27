package com.mindex.challenge.data;

import java.time.LocalDate;

public class Compensation {
    private Employee employee;

    private String employeeId;
    private double salary;
    private LocalDate effectiveDate;

    public Compensation(Employee employee, double salary, LocalDate effectiveDate) {
        this.employee = employee;
        this.employeeId = employee.getEmployeeId();
        this.salary = salary;
        this.effectiveDate = effectiveDate;
    }


    public Employee getEmployee() {
        return employee;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
}
