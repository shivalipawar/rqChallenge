package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.EmployeeNotFoundException;
import com.example.rqchallenge.exception.HighestSalaryNotFound;
import com.example.rqchallenge.exception.TooManyRequestException;
import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeeCreateResponse;
import com.example.rqchallenge.models.EmployeeDeleteResponse;
import com.example.rqchallenge.models.EmployeesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    //TODO move to property file
    private static final String HOSTNAME = "https://dummy.restapiexample.com/api/v1";
    private static final String GET_EMPLOYEES = "/employees";
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
            if(response.getStatusLine().getStatusCode() == 429)
                throw new TooManyRequestException();
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
        List<Employee> employeeById = allEmployees.stream().filter(employee -> employee.getId().equalsIgnoreCase(id)).collect(Collectors.toList());
        if(employeeById.isEmpty()){
            throw new EmployeeNotFoundException(id);
        }
        return employeeById.get(0);
    }

    public Long getHighestSalaryOfEmployee() {
        List<Employee> allEmployees = getAllEmployees();
        List<Employee> highestSalaryEmployee = allEmployees.stream().sorted(Comparator.comparingLong(Employee::getSalary).reversed()).limit(1).collect(Collectors.toList());
        if(highestSalaryEmployee.isEmpty()){
            throw new HighestSalaryNotFound();
        }
        return highestSalaryEmployee.get(0).getSalary();
    }

    public List<String> getTenHighestSalaryEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream().sorted(Comparator.comparingLong(Employee::getSalary).reversed()).map(Employee::getName).limit(10).collect(Collectors.toList());
    }

    public Employee createEmployee(Map<String, Object> employeeInput){
        HttpPost post = new HttpPost(HOSTNAME + CREATE);
        EmployeeCreateResponse employeesResponse;
        try {
            Employee employee = convertInputToEquivalentPOJO(employeeInput, post);
            CloseableHttpResponse response = closeableHttpClient.execute(post);
            if(response.getStatusLine().getStatusCode() == 429)
                throw new TooManyRequestException();

            String responseEntity = EntityUtils.toString(response.getEntity());
            employeesResponse = objectMapper.readValue(responseEntity, EmployeeCreateResponse.class);
            updateCreatedEmployee(employeesResponse, employee);
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

    private Employee convertInputToEquivalentPOJO(Map<String, Object> employeeInput, HttpPost post) throws JsonProcessingException, UnsupportedEncodingException {
        String employeeInputString = objectMapper.writeValueAsString(employeeInput);
        Employee employee = objectMapper.readValue(employeeInputString, Employee.class);
        post.setEntity(new StringEntity(employee.toString()));
        return employee;
    }

    private void updateCreatedEmployee(EmployeeCreateResponse employeesResponse, Employee employee) {
        if(employeesResponse.getStatus().equalsIgnoreCase("success")){
            Employee addedEmployee = employeesResponse.getData();
            addedEmployee.setName(employee.getName());
            addedEmployee.setSalary(employee.getSalary());
            addedEmployee.setAge(employee.getAge());
        }
    }

    public EmployeeDeleteResponse deleteEmployeeById(String id){
        HttpDelete delete = new HttpDelete(HOSTNAME + DELETE + id);
        EmployeeDeleteResponse employeesResponse;
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(delete);
            if(response.getStatusLine().getStatusCode() == 429)
                throw new TooManyRequestException();
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
