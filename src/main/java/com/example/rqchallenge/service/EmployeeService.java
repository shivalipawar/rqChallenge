package com.example.rqchallenge.service;

import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    //TODO move to property file
    public static final String HOSTNAME = "https://dummy.restapiexample.com/api/v1";
    public static final String GET_EMPLOYEES = "/employees";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    public List<Employee> getAllEmployees() throws IOException {
        HttpGet get = new HttpGet(HOSTNAME + GET_EMPLOYEES);
        CloseableHttpResponse response = closeableHttpClient.execute(get);
        String responseEntity = EntityUtils.toString(response.getEntity());
        EmployeesResponse employeesResponse = objectMapper.readValue(responseEntity, EmployeesResponse.class);
        return employeesResponse.getData();
    }

    public List<Employee> getEmployeesByName() throws IOException {
        HttpGet get = new HttpGet(HOSTNAME + "/employees");
        CloseableHttpResponse response = closeableHttpClient.execute(get);
        String responseEntity = EntityUtils.toString(response.getEntity());
        return new ArrayList<>();
    }

}
