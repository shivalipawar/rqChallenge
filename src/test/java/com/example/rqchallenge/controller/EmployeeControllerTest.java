package com.example.rqchallenge.controller;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.HighestSalaryNotFound;
import com.example.rqchallenge.exception.TooManyRequestException;
import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeeDeleteResponse;
import com.example.rqchallenge.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    private List<Employee> employees;

    @Autowired
    EmployeeController employeeController;

    @MockBean
    EmployeeService employeeService;

    private List<Employee> createTestEmployeesList() {
        Employee employee = new Employee();
        employee.setName("Ashley Rock");
        employee.setAge(23);
        employee.setSalary(207000L);
        employee.setId("1");
        Employee employee2 = new Employee();
        employee2.setName("Rush Rouge");
        employee2.setAge(45);
        employee2.setSalary(400800L);
        employee2.setId("2");
        Employee employee3 = new Employee();
        employee3.setName("Peace Liam");
        employee3.setAge(51);
        employee3.setSalary(150050L);
        employee3.setId("3");
        Employee employee4 = new Employee();
        employee4.setName("Bean Kyle");
        employee4.setAge(22);
        employee4.setSalary(750050L);
        employee4.setId("4");
        Employee employee5 = new Employee();
        employee5.setName("Peace Chrip");
        employee5.setAge(57);
        employee5.setSalary(230050L);
        employee5.setId("5");
        return Arrays.asList(employee,employee2,employee3,employee4,employee5);
    }

    @BeforeAll
    public void init(){
        employees = createTestEmployeesList();
    }

    @Test
    void getAllEmployeesShouldReturnSuccess() {

        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);
        ResponseEntity<List<Employee>> allEmployeesResponse = employeeController.getAllEmployees();

        Assertions.assertNotNull(allEmployeesResponse);
        Assertions.assertEquals(HttpStatus.OK,allEmployeesResponse.getStatusCode());
    }

    @Test
    void getAllEmployeesShouldReturnExceptionForTooManyRequest() {

        Mockito.when(employeeService.getAllEmployees()).thenThrow(TooManyRequestException.class);
        TooManyRequestException exception = Assertions.assertThrows(
                TooManyRequestException.class,
                () -> employeeController.getAllEmployees(),
                "Too Many Request exception expected"
        );
        Assertions.assertNotNull(exception);
//        Assertions.assertEquals("Too many request",exception.getMessage());
    }

    @Test
    void getEmployeesByNameSearchSuccess() {
        String empToSearch = "Peace";
        List<Employee> employeeNamed = Arrays.asList(employees.get(2),employees.get(4));

        Mockito.when(employeeService.getEmployeesByName(empToSearch)).thenReturn(employeeNamed);
        ResponseEntity<List<Employee>> allEmployeesResponse = employeeController.getEmployeesByNameSearch(empToSearch);

        Assertions.assertNotNull(allEmployeesResponse);
        Assertions.assertNotNull(allEmployeesResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK,allEmployeesResponse.getStatusCode());
        Assertions.assertEquals(2,allEmployeesResponse.getBody().size());
    }

    @Test
    void getEmployeeByIdSuccess() {
        String empToById = "3";
        Employee employeeNamed = employees.get(2);

        Mockito.when(employeeService.getEmployeeById(empToById)).thenReturn(employeeNamed);
        ResponseEntity<Employee> employeeResponse = employeeController.getEmployeeById(empToById);

        Assertions.assertNotNull(employeeResponse);
        Assertions.assertNotNull(employeeResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK,employeeResponse.getStatusCode());
        Assertions.assertEquals(employeeNamed.getName(),employeeResponse.getBody().getName());
        Assertions.assertEquals(employeeNamed.getAge(),employeeResponse.getBody().getAge());
        Assertions.assertEquals(employeeNamed.getSalary(),employeeResponse.getBody().getSalary());
    }

    @Test
    void getHighestSalaryOfEmployeesSuccess() {

        Mockito.when(employeeService.getHighestSalaryOfEmployee()).thenReturn(employees.get(3).getSalary());
        ResponseEntity<Integer> highestSalaryResponse = employeeController.getHighestSalaryOfEmployees();

        Assertions.assertNotNull(highestSalaryResponse);
        Assertions.assertNotNull(highestSalaryResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK,highestSalaryResponse.getStatusCode());
        Assertions.assertEquals(750050L,highestSalaryResponse.getBody().longValue());
    }

    @Test
    void getHighestSalaryOfEmployeesThrowsException() {

        Mockito.when(employeeService.getHighestSalaryOfEmployee()).thenThrow(HighestSalaryNotFound.class);

        HighestSalaryNotFound exception = Assertions.assertThrows(
                HighestSalaryNotFound.class,
                () -> employeeController.getHighestSalaryOfEmployees(),
                "Expected highest salary not found exception"
        );

        Assertions.assertNotNull(exception);
    }

    @Test
    void getTopTenHighestEarningEmployeeNamesSuccess() {
        List<String> employeeName = employees.stream().map(Employee::getName).collect(Collectors.toList());

        Mockito.when(employeeService.getTenHighestSalaryEmployeeNames()).thenReturn(employeeName);
        ResponseEntity<List<String>> employeeWithHighestSalaryResponse = employeeController.getTopTenHighestEarningEmployeeNames();

        Assertions.assertNotNull(employeeWithHighestSalaryResponse);
        Assertions.assertNotNull(employeeWithHighestSalaryResponse.getBody());
        Assertions.assertEquals(HttpStatus.OK,employeeWithHighestSalaryResponse.getStatusCode());
        Assertions.assertEquals(employeeName.size(),employeeWithHighestSalaryResponse.getBody().size());
        List<String> responseNames = employeeWithHighestSalaryResponse.getBody();
        Assertions.assertEquals(employeeName, responseNames);
    }

    @Test
    void createEmployeeSuccess() {
        Map<String, Object> employeeToCreateInMap = getEmployeeToCreateInMap();
        Employee empCreated = getCreatedEmp();

        Mockito.when(employeeService.createEmployee(employeeToCreateInMap)).thenReturn(empCreated);
        ResponseEntity<Employee> employeeResponseEntity = employeeController.createEmployee(employeeToCreateInMap);

        Assertions.assertNotNull(employeeResponseEntity);
        Assertions.assertEquals(HttpStatus.OK,employeeResponseEntity.getStatusCode());
        Assertions.assertEquals(empCreated, employeeResponseEntity.getBody());
    }

    private Map<String, Object> getEmployeeToCreateInMap() {
        Map<String, Object> employeeToCreateInMap = new HashMap<>();
        employeeToCreateInMap.put("id", "6");
        employeeToCreateInMap.put("employee_name", "Hanna Montana");
        employeeToCreateInMap.put("employee_age", 61);
        employeeToCreateInMap.put("employee_salary", 6700540L);
        return employeeToCreateInMap;
    }

    private Employee getCreatedEmp() {
        Employee empCreated = new Employee();
        empCreated.setSalary(6700540L);
        empCreated.setId("6");
        empCreated.setName("Hanna Montana");
        empCreated.setAge(61);
        return empCreated;
    }

    @Test
    void createEmployeeThrowsTooManyRequestException() {
        Map<String, Object> employeeToCreateInMap = getEmployeeToCreateInMap();

        Employee empCreated = getCreatedEmp();

        Mockito.when(employeeService.createEmployee(employeeToCreateInMap)).thenReturn(empCreated);
        ResponseEntity<Employee> employeeResponseEntity = employeeController.createEmployee(employeeToCreateInMap);

        Assertions.assertNotNull(employeeResponseEntity);
        Assertions.assertEquals(HttpStatus.OK,employeeResponseEntity.getStatusCode());
        Assertions.assertEquals(empCreated, employeeResponseEntity.getBody());
    }

    @Test
    void deleteEmployeeByIdSuccess() {
        String empToById = "5";
        Employee empToBeDeleted = employees.get(4);
        EmployeeDeleteResponse employeeDeleteResponse = new EmployeeDeleteResponse();
        employeeDeleteResponse.setData(empToById);
        employeeDeleteResponse.setMessage("Successfully deleted!");
        employeeDeleteResponse.setStatus("success");

        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);
        Mockito.when(employeeService.getEmployeeById(empToById)).thenReturn(empToBeDeleted);
        Mockito.when(employeeService.deleteEmployeeById(empToById)).thenReturn(employeeDeleteResponse);
        ResponseEntity<String> employee = employeeController.deleteEmployeeById(empToById);

        Assertions.assertNotNull(employee);
        Assertions.assertEquals(HttpStatus.OK,employee.getStatusCode());
        Assertions.assertEquals(empToBeDeleted.getName(),employee.getBody());
    }

    @Test
    public void deleteEmployeeByIdFailsWhenIdNotFound(){
        String empToById = "6";

        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);
        Mockito.when(employeeService.getEmployeeById(empToById)).thenThrow(EmployeeNotFoundException.class);
        EmployeeNotFoundException exception = Assertions.assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeController.deleteEmployeeById(empToById),
                "Expected Employee Not Found exception"
        );
        Assertions.assertNotNull(exception);
//        Assertions.assertEquals("Employee Not Found",exception.getMessage());
    }

    @Test
    public void deleteEmployeeThrowsTooManyRequestException(){

        String empToById = "5";
        Employee empToBeDeleted = employees.get(4);

        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);
        Mockito.when(employeeService.getEmployeeById(empToById)).thenReturn(empToBeDeleted);
        Mockito.when(employeeService.deleteEmployeeById(empToById)).thenThrow(TooManyRequestException.class);

        TooManyRequestException exception = Assertions.assertThrows(
                TooManyRequestException.class,()-> employeeController.deleteEmployeeById(empToById), "Expected Too many request exception"
        );
        Assertions.assertNotNull(exception);
//        Assertions.assertEquals("Too many requests",exception.getMessage());
    }
}