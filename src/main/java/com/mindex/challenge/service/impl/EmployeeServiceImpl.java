package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String employeeId) {
        LOG.debug("Fetching employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /* Returns a ReportingStructure object that represents the reporting chain for a given employee */
    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        Employee employee = read(employeeId);
        int numberOfReports = calculateNumberOfReports(employee);
        return new ReportingStructure(employee, numberOfReports);
    }

    /* Recursively calculates the number of employees who report to the given employee
       Makes one database call for each employee. This could be very inefficient for a large
       organization. One solution would be to make one database call to retrieve all employees
       in the organization at once and build the hierarchy from there. Without more knowledge of the
       use-case, we'll go with the simpler option. Assumes no cycles in the employee tree.
     */
    private int calculateNumberOfReports(Employee employee) {
        if (employee.getDirectReports() == null) {
            return 0;
        }

        int count = employee.getDirectReports().size();
        for (Employee directReport : employee.getDirectReports()) {
            Employee fullDirectReport = read(directReport.getEmployeeId());
            count += calculateNumberOfReports(fullDirectReport);
        }
        return count;
    }
}
