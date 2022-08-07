package com.example.rqchallenge.controller;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.HighestSalaryNotFoundException;
import com.example.rqchallenge.exception.TooManyRequestException;
import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeeDeleteResponse;
import com.example.rqchallenge.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    private List<Employee> employees;

    @Autowired
    EmployeeController employeeController;

    @MockBean
    EmployeeService employeeService;

    private List<Employee> sampleEmployees() {
        return Arrays.asList(
                new Employee("1", "Ashley Rock", 207000L, 23, ""),
                new Employee("2", "Rush Rouge", 400800L, 45, ""),
                new Employee("3", "Peace Liam", 150050L, 51, ""),
                new Employee("4", "Bean Kyle", 750050L, 22, ""),
                new Employee("5", "Peace Chrip", 230050L, 57, "")
        );
    }

    @BeforeAll
    public void init() {
        employees = sampleEmployees();
    }

    @Test
    void getAllEmployeesShouldReturnSuccess() {

        when(employeeService.getAllEmployees()).thenReturn(employees);
        ResponseEntity<List<Employee>> allEmployeesResponse = employeeController.getAllEmployees();

        assertNotNull(allEmployeesResponse);
        assertEquals(HttpStatus.OK, allEmployeesResponse.getStatusCode());
        List<Employee> employeesResponse = allEmployeesResponse.getBody();
        assertEquals(employees, employeesResponse);
    }

    @Test
    void getAllEmployeesShouldReturnExceptionForTooManyRequest() {

        when(employeeService.getAllEmployees()).thenThrow(new TooManyRequestException());
        TooManyRequestException exception = Assertions.assertThrows(
                TooManyRequestException.class,
                () -> employeeController.getAllEmployees(),
                "Too Many Request exception expected"
        );
        assertNotNull(exception);
        assertEquals("Too many requests", exception.getMessage());
    }

    @Test
    void getEmployeesByNameSearchSuccess() {
        String empToSearch = "Peace";
        List<Employee> employeeNamed = Arrays.asList(employees.get(2), employees.get(4));

        when(employeeService.getEmployeesByName(empToSearch)).thenReturn(employeeNamed);
        ResponseEntity<List<Employee>> allEmployeesResponse = employeeController.getEmployeesByNameSearch(empToSearch);

        assertNotNull(allEmployeesResponse);
        assertNotNull(allEmployeesResponse.getBody());
        assertEquals(HttpStatus.OK, allEmployeesResponse.getStatusCode());
        assertEquals(2, allEmployeesResponse.getBody().size());
    }

    @Test
    void getEmployeeByIdSuccess() {
        String empToById = "3";
        Employee employeeNamed = employees.get(2);

        when(employeeService.getEmployeeById(empToById)).thenReturn(employeeNamed);
        ResponseEntity<Employee> employeeResponse = employeeController.getEmployeeById(empToById);

        assertNotNull(employeeResponse);
        Employee responseBody = employeeResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, employeeResponse.getStatusCode());
        assertEquals(employeeNamed.getName(), responseBody.getName());
        assertEquals(employeeNamed.getAge(), responseBody.getAge());
        assertEquals(employeeNamed.getSalary(), responseBody.getSalary());
    }

    @Test
    void getHighestSalaryOfEmployeesSuccess() {

        when(employeeService.getHighestSalaryOfEmployee()).thenReturn(employees.get(3).getSalary());
        ResponseEntity<Integer> highestSalaryResponse = employeeController.getHighestSalaryOfEmployees();

        assertNotNull(highestSalaryResponse);
        assertNotNull(highestSalaryResponse.getBody());
        assertEquals(HttpStatus.OK, highestSalaryResponse.getStatusCode());
        assertEquals(750050L, highestSalaryResponse.getBody().longValue());
    }

    @Test
    void getHighestSalaryOfEmployeesThrowsException() {

        when(employeeService.getHighestSalaryOfEmployee()).thenThrow(new HighestSalaryNotFoundException());

        HighestSalaryNotFoundException exception = Assertions.assertThrows(
                HighestSalaryNotFoundException.class,
                () -> employeeController.getHighestSalaryOfEmployees(),
                "Expected highest salary not found exception"
        );
        assertEquals("Employee with highest salary not found", exception.getMessage());
    }

    @Test
    void getTopTenHighestEarningEmployeeNamesSuccess() {
        List<String> employeeName = employees.stream().map(Employee::getName).collect(Collectors.toList());

        when(employeeService.getTenHighestSalaryEmployeeNames()).thenReturn(employeeName);
        ResponseEntity<List<String>> employeeWithHighestSalaryResponse = employeeController.getTopTenHighestEarningEmployeeNames();

        assertNotNull(employeeWithHighestSalaryResponse);
        assertNotNull(employeeWithHighestSalaryResponse.getBody());
        assertEquals(HttpStatus.OK, employeeWithHighestSalaryResponse.getStatusCode());
        assertEquals(employeeName.size(), employeeWithHighestSalaryResponse.getBody().size());
        List<String> responseNames = employeeWithHighestSalaryResponse.getBody();
        assertEquals(employeeName, responseNames);
    }

    @Test
    void createEmployeeSuccess() {
        Map<String, Object> employeeToCreateInMap = sampleEmployeeDetails();
        Employee empCreated = getSampleEmployee();

        when(employeeService.createEmployee(employeeToCreateInMap)).thenReturn(empCreated);
        ResponseEntity<Employee> employeeResponseEntity = employeeController.createEmployee(employeeToCreateInMap);

        assertNotNull(employeeResponseEntity);
        assertEquals(HttpStatus.OK, employeeResponseEntity.getStatusCode());
        assertEquals(empCreated, employeeResponseEntity.getBody());
    }

    @Test
    void deleteEmployeeByIdSuccess() {
        String empToById = "5";
        Employee empToBeDeleted = employees.get(4);
        EmployeeDeleteResponse employeeDeleteResponse = new EmployeeDeleteResponse();
        employeeDeleteResponse.setData(empToById);
        employeeDeleteResponse.setMessage("Successfully deleted!");
        employeeDeleteResponse.setStatus("success");

        when(employeeService.getEmployeeById(empToById)).thenReturn(empToBeDeleted);
        when(employeeService.deleteEmployeeById(empToById)).thenReturn(employeeDeleteResponse);

        ResponseEntity<String> employee = employeeController.deleteEmployeeById(empToById);

        assertNotNull(employee);
        assertEquals(HttpStatus.OK, employee.getStatusCode());
        assertEquals(empToBeDeleted.getName(), employee.getBody());
    }

    @Test
    public void deleteEmployeeByIdFailsWhenIdNotFound() {
        String employeeId = "6";

        when(employeeService.deleteEmployeeById(employeeId))
                .thenThrow(new EmployeeNotFoundException(employeeId));

        EmployeeNotFoundException exception = Assertions.assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeController.deleteEmployeeById(employeeId),
                "Expected Employee Not Found exception"
        );
        assertNotNull(exception);
        Assertions.assertEquals("Employee with Id 6 not found", exception.getMessage());
    }

    @Test
    public void deleteEmployeeThrowsTooManyRequestException() {

        String empToById = "5";
        when(employeeService.deleteEmployeeById(empToById)).thenThrow(new TooManyRequestException());

        TooManyRequestException exception = Assertions.assertThrows(
                TooManyRequestException.class, () -> employeeController.deleteEmployeeById(empToById), "Expected Too many request exception"
        );
        assertNotNull(exception);
        Assertions.assertEquals("Too many requests", exception.getMessage());
    }

    @Test
    void createEmployeeThrowsTooManyRequestException() {
        Map<String, Object> employeeToCreateInMap = sampleEmployeeDetails();

        Employee empCreated = getSampleEmployee();

        when(employeeService.createEmployee(employeeToCreateInMap)).thenReturn(empCreated);
        ResponseEntity<Employee> employeeResponseEntity = employeeController.createEmployee(employeeToCreateInMap);

        assertNotNull(employeeResponseEntity);
        assertEquals(HttpStatus.OK, employeeResponseEntity.getStatusCode());
        assertEquals(empCreated, employeeResponseEntity.getBody());
    }

    private Map<String, Object> sampleEmployeeDetails() {
        Map<String, Object> employeeToCreateInMap = new HashMap<>();
        employeeToCreateInMap.put("id", "6");
        employeeToCreateInMap.put("employee_name", "Hanna Montana");
        employeeToCreateInMap.put("employee_age", 61);
        employeeToCreateInMap.put("employee_salary", 6700540L);
        return employeeToCreateInMap;
    }

    private Employee getSampleEmployee() {
        return new Employee("6", "Hanna Montana", 6700540L, 61, "");
    }
}