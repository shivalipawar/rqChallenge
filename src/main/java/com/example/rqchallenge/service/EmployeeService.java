package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.TooManyRequestException;
import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeeCreateResponse;
import com.example.rqchallenge.models.EmployeeDeleteResponse;
import com.example.rqchallenge.models.EmployeesResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
        EmployeesResponse employeesResponse;
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200)
                throw new TooManyRequestException("Too many requests");
            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeesResponse.class);
        } catch (TooManyRequestException e){
            //TODO Logger
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return employeesResponse.getData();
    }

    public List<Employee> getEmployeesByName(String searchString) {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream().filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase())).collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        List<Employee> allEmployees = getAllEmployees();
        List<Employee> collect = allEmployees.stream().filter(employee -> employee.getId().equalsIgnoreCase(id)).collect(Collectors.toList());
        return (!collect.isEmpty()) ? collect.get(0) : null;
    }

    public Long getHighestSalaryOfEmployee() {
        List<Employee> allEmployees = getAllEmployees();
        List<Employee> highestSalaryEmployee = allEmployees.stream().sorted(Comparator.comparingLong(Employee::getSalary).reversed()).limit(1).collect(Collectors.toList());
        return (!highestSalaryEmployee.isEmpty()) ? highestSalaryEmployee.get(0).getSalary() : null;
    }

    public List<String> getTenHighestSalaryEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream().sorted(Comparator.comparingLong(Employee::getSalary).reversed()).map(Employee::getName).limit(10).collect(Collectors.toList());
    }

    public Employee createEmployee(Map<String, Object> employeeInput){
        HttpPost post = new HttpPost(HOSTNAME + CREATE);
        EmployeeCreateResponse employeesResponse;
        try {
            String employeeInputString = objectMapper.writeValueAsString(employeeInput);
            Employee employee = objectMapper.readValue(employeeInputString, Employee.class);
            post.setEntity(new StringEntity(employee.toString()));
            CloseableHttpResponse response = closeableHttpClient.execute(post);
            if(response.getStatusLine().getStatusCode() != 200)
                throw new TooManyRequestException("Too many requests");
            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeeCreateResponse.class);
            if(employeesResponse.getStatus().equalsIgnoreCase("success")){
                Employee addedEmployee = employeesResponse.getData();
                addedEmployee.setName(employee.getName());
                addedEmployee.setSalary(employee.getSalary());
                addedEmployee.setAge(employee.getAge());
            }
        } catch (TooManyRequestException e){
            //TODO Logger
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return employeesResponse.getData();
    }

    public EmployeeDeleteResponse deleteEmployeeById(String id){
        HttpDelete delete = new HttpDelete(HOSTNAME + DELETE + id);
        EmployeeDeleteResponse employeesResponse;
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(delete);
            if(response.getStatusLine().getStatusCode() != 200)
                throw new TooManyRequestException("Too many requests");
            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeeDeleteResponse.class);
        } catch (TooManyRequestException e){
            //TODO Logger
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return employeesResponse;
    }

}
