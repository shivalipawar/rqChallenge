package com.example.rqchallenge.service;

import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    //TODO move to property file
    public static final String HOSTNAME = "https://dummy.restapiexample.com/api/v1";
    public static final String GET_EMPLOYEES = "/employees";
    private static final String CREATE = "/create";
    private static final String DELETE = "/delete/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    public List<Employee> getAllEmployees() {
        HttpGet get = new HttpGet(HOSTNAME + GET_EMPLOYEES);
        EmployeesResponse employeesResponse = null;
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(get);
            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeesResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return employeesResponse.getData();
    }

    public List<Employee> getEmployeesByName(String searchString) {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream().filter(employee -> employee.getName().equalsIgnoreCase(searchString)).collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream().filter(employee -> employee.getId().equalsIgnoreCase(id)).collect(Collectors.toList()).get(0);
    }

    public Long getHighestSalaryOfEmployee() {
        List<Employee> allEmployees = getAllEmployees();
        List<Employee> highestSalaryEmployee = allEmployees.stream().sorted(Comparator.comparingLong(Employee::getSalary).reversed()).limit(1).collect(Collectors.toList());
        return highestSalaryEmployee.get(0).getSalary();
    }

    public List<String> getTenHighestSalaryEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream().sorted(Comparator.comparingLong(Employee::getSalary).reversed()).map(Employee::getName).limit(10).collect(Collectors.toList());
    }

    public Employee createEmployee(Map<String, Object> employeeInput){
        HttpPost post = new HttpPost(HOSTNAME + CREATE);
        EmployeesResponse employeesResponse = null;
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(post);
            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeesResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return employeesResponse.getData().get(0);
    }

    public EmployeesResponse deleteEmployeeById(String id){
        HttpDelete delete = new HttpDelete(HOSTNAME + DELETE + id);
        EmployeesResponse employeesResponse = null;
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(delete);
            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeesResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return employeesResponse;
    }

}
