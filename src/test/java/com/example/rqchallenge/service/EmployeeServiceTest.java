package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.ApiFailureException;
import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.HighestSalaryNotFoundException;
import com.example.rqchallenge.exception.TooManyRequestException;
import com.example.rqchallenge.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeServiceTest {

    private List<Employee> employees;
    private ObjectMapper objectMapper;
    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private CloseableHttpClient closeableHttpClient;

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
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllEmployeesSuccess() throws IOException {
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employees, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        List<Employee> allEmployees = employeeService.getAllEmployees();
        assertNotNull(allEmployees);
        assertEquals(employees, allEmployees);
    }

    @Test
    void getAllEmployeesSuccessThrowsTooManyRequestException() throws IOException {
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employees, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);
        when(mockStatusLine.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS.value());

        TooManyRequestException exception = Assertions.assertThrows(
                TooManyRequestException.class,
                () -> employeeService.getAllEmployees(),
                "Expected Too many request exception"
        );
        assertEquals("Too many requests",exception.getMessage());
    }

    @Test
    void getEmployeesByNameSuccess() throws IOException {
        String employeeName = "Peace";
        List<Employee> employeesExpected = Arrays.asList(employees.get(2),employees.get(4));
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employeesExpected, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        List<Employee> allEmployees = employeeService.getEmployeesByName(employeeName);
        assertNotNull(allEmployees);
        assertEquals(employeesExpected, allEmployees);
    }

    @Test
    void getEmployeeByIdSuccess() throws IOException {
        String employeeId = "4";
        List<Employee> employeesExpected = Collections.singletonList(employees.get(3));
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employeesExpected, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        Employee allEmployees = employeeService.getEmployeeById(employeeId);
        assertNotNull(allEmployees);
        assertEquals(employeesExpected.get(0), allEmployees);
    }

    @Test
    void getEmployeeByIdThrowEmployeeNotFoundException() throws IOException {
        String employeeId = "4";
        List<Employee> employeesExpected = Collections.singletonList(new Employee("7","Sarah Jone",230000L,28,""));
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employeesExpected, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        EmployeeNotFoundException exception = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId),
                "Expected employee not found exception"
        );

        assertEquals("Employee with Id 4 not found",exception.getMessage());
    }

    @Test
    void getHighestSalaryOfEmployeeSuccess() throws IOException {
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employees, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        Long highestSalaryOfEmployee = employeeService.getHighestSalaryOfEmployee();
        assertNotNull(highestSalaryOfEmployee);
        assertEquals(employees.get(3).getSalary(), highestSalaryOfEmployee);
    }

    @Test
    void getHighestSalaryOfEmployeeThrowException() throws IOException {
        EmployeesResponse employeesResponse = new EmployeesResponse("200", new ArrayList<>(), "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        HighestSalaryNotFoundException exception = assertThrows(
                HighestSalaryNotFoundException.class,
                () -> employeeService.getHighestSalaryOfEmployee(),
                "Expected highest salary employee exception"
        );

        assertEquals("Employee with highest salary not found", exception.getMessage());
    }

    @Test
    void getTenHighestSalaryEmployeeNamesSuccess() throws IOException {
        EmployeesResponse employeesResponse = new EmployeesResponse("200", employees, "");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        List<String> employeesNames = employees.stream().map(Employee::getName).collect(Collectors.toList());

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeesResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        List<String> employeesNameResponse = employeeService.getTenHighestSalaryEmployeeNames();
        assertNotNull(employeesNameResponse);
        assertTrue(employeesNames.size() == employeesNameResponse.size()
                && employeesNames.containsAll(employeesNameResponse)
                && employeesNameResponse.containsAll(employeesNames));
    }

    @Test
    void createEmployeeSuccess() throws IOException {
        Map<String, Object> employeeInput = sampleEmployeeDetails();
        CreateEmployeeApiResponse emp = new CreateEmployeeApiResponse("7", "Sarah Jone", 230000L, 28);
        EmployeeCreateResponse employeeCreateResponse = new EmployeeCreateResponse("200",emp,"");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeeCreateResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        Employee employeesActualResponse = employeeService.createEmployee(employeeInput);
        assertEquals(emp.getAge(),employeesActualResponse.getAge());
        assertEquals(emp.getSalary(),employeesActualResponse.getSalary());
        assertEquals(emp.getName(),employeesActualResponse.getName());
    }

    @Test
    void createEmployeeThrowsException() throws IOException {
        Map<String, Object> employeeInput = sampleEmployeeDetails();
        CreateEmployeeApiResponse emp = new CreateEmployeeApiResponse("7", "Sarah Jone", 230000L, 28);
        EmployeeCreateResponse employeeCreateResponse = new EmployeeCreateResponse("200",emp,"");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(employeeCreateResponse)));
        when(closeableHttpClient.execute(Mockito.any())).thenThrow(new ApiFailureException("json parsing exception"));

        ApiFailureException exception = assertThrows(
                ApiFailureException.class,
                () -> employeeService.createEmployee(employeeInput),
                "Expected api failure exception"
        );
        assertEquals("json parsing exception",exception.getMessage());

    }

    @Test
    void deleteEmployeeByIdSuccess() throws IOException {
        String empId = "1";
        EmployeeDeleteResponse emp = new EmployeeDeleteResponse("success", "1", "Successfully deleted");
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);

        when(mockResponse.getStatusLine()).thenReturn(mock(StatusLine.class));
        when(mockResponse.getEntity()).thenReturn(new StringEntity(objectMapper.writeValueAsString(emp)));
        when(closeableHttpClient.execute(Mockito.any())).thenReturn(mockResponse);

        EmployeeDeleteResponse employeesActualResponse = employeeService.deleteEmployeeById(empId);
        assertEquals(emp.getData(),employeesActualResponse.getData());
        assertEquals(emp.getStatus(),employeesActualResponse.getStatus());
        assertEquals(emp.getMessage(),employeesActualResponse.getMessage());
    }

    private Map<String, Object> sampleEmployeeDetails() {
        Map<String, Object> employeeToCreateInMap = new HashMap<>();
        employeeToCreateInMap.put("id", "6");
        employeeToCreateInMap.put("employee_name", "Hanna Montana");
        employeeToCreateInMap.put("employee_age", 61);
        employeeToCreateInMap.put("employee_salary", 6700540L);
        return employeeToCreateInMap;
    }
}