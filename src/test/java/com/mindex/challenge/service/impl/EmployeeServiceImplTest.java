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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

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
    public void testReportingStructure() {

        /**
         *  Test employee structure:           manager
         *                             report1         report2
         *                             intern
         */
        Employee intern = new Employee();
        intern.setFirstName("Intern");
        intern = restTemplate.postForEntity(employeeUrl, intern, Employee.class).getBody();

        Employee report1 = new Employee();
        report1.setFirstName("Report 1");
        List<Employee> report1Reports = new ArrayList<>();
        report1Reports.add(intern);
        report1.setDirectReports(report1Reports);
        report1 = restTemplate.postForEntity(employeeUrl, report1, Employee.class).getBody();

        Employee report2 = new Employee();
        report2.setFirstName("Report 2");
        report2 = restTemplate.postForEntity(employeeUrl, report2, Employee.class).getBody();

        Employee manager = new Employee();
        manager.setFirstName("Manager");
        List<Employee> managerReports = new ArrayList<>();
        managerReports.add(report1);
        managerReports.add(report2);
        manager.setDirectReports(managerReports);
        manager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();

        ReportingStructure managerReportStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, manager.getEmployeeId()).getBody();
        assertEquals(Integer.valueOf(3), managerReportStructure.getNumberOfReports());
        assertEmployeeEquivalence(manager, managerReportStructure.getEmployee());

        ReportingStructure report1ReportStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, report1.getEmployeeId()).getBody();
        assertEquals(Integer.valueOf(1), report1ReportStructure.getNumberOfReports());
        assertEmployeeEquivalence(report1, report1ReportStructure.getEmployee());

        ReportingStructure report2ReportStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, report2.getEmployeeId()).getBody();
        assertEquals(Integer.valueOf(0), report2ReportStructure.getNumberOfReports());
        assertEmployeeEquivalence(report2, report2ReportStructure.getEmployee());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
