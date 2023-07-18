package com.mindex.challenge.service.impl;


import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {
    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{employeeId}";
    }

    @Test
    public void testCreateReadCompensation() {
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("123");
        Date effectiveDate = Date.from(Instant.now());
        Compensation compensation = new Compensation();
        compensation.setEmployee(testEmployee);
        compensation.setEffectiveDate(effectiveDate);
        compensation.setSalary("1000");

        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        assertNotNull(createdCompensation);
        assertEquals(createdCompensation.getEmployee().getEmployeeId(), testEmployee.getEmployeeId());
        assertNotNull(createdCompensation.getEffectiveDate());
        assertNotNull(createdCompensation.getSalary());

        Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, "123").getBody();
        assertNotNull(readCompensation);
        assertEquals(readCompensation.getEmployee().getEmployeeId(), testEmployee.getEmployeeId());
        assertEquals(readCompensation.getSalary(), "1000");
    }
}
