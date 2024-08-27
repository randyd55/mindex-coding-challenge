package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/reporting-structure/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = createTestEmployee("John", "Doe", "Engineering", "Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testReadReportingStructure() {
        Employee employee1 = employeeService.read("16a596ae-edd3-4847-99fe-c4518e82c86f");

        // Test reporting structure for employee1
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee1.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(employee1.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(4, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testCreateReportingStructure() {
        // Create test employees
        Employee employee1 = createTestEmployee("Eleanor", "Rigby", "Engineering","Manager");
        Employee employee2 = createTestEmployee("Mister", "Kite", "Engineering", "Developer");
        Employee employee3 = createTestEmployee("Penny", "Lane", "Engineering","Developer");

        // Set up reporting structure
        employee1.setDirectReports(Arrays.asList(employee2, employee3));
        employeeService.update(employee1);

        // Test reporting structure for employee1
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee1.getEmployeeId()).getBody();

        assertNotNull(reportingStructure);
        assertEquals(employee1.getEmployeeId(), reportingStructure.getEmployee().getEmployeeId());
        assertEquals(2, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testReportingStructureNoReporters() {
        // Create test employees
        Employee employee1 = createTestEmployee("Eleanor", "Rigby", "Engineering","Manager");
        Employee employee2 = createTestEmployee("Mister", "Kite", "Engineering", "Developer");
        Employee employee3 = createTestEmployee("Penny", "Lane", "Engineering","Developer");

        // Set up reporting structure
        employee1.setDirectReports(Arrays.asList(employee2, employee3));
        employeeService.update(employee1);

        // Test reporting structure for employee2 (no direct reports)
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee2.getEmployeeId()).getBody();

        assertEquals(0, reportingStructure.getNumberOfReports());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private Employee createTestEmployee(String firstName, String lastName, String department, String position) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPosition(position);
        employee.setDepartment(department);
        return employeeService.create(employee);
    }
}
