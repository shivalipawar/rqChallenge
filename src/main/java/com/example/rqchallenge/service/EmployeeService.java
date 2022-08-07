package com.example.rqchallenge.service;

import com.example.rqchallenge.exception.*;
import com.example.rqchallenge.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    public static final int EMPLOYEE_PAGE_SIZE = 10;
    @Value("${hostname}")
    private String HOSTNAME;

    @Value("${api.version}")
    private String API_VERSION;

    @Value("${employee.get.all}")
    private String GET_EMPLOYEES;

    @Value("${employee.delete}")
    private String DELETE_EMPLOYEE;

    @Value("${employee.create}")
    private String CREATE_EMPLOYEE;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    public List<Employee> getAllEmployees() {
        try {
            String getEmployeesUrl = HOSTNAME + API_VERSION + GET_EMPLOYEES;
            CloseableHttpResponse response = closeableHttpClient.execute(new HttpGet(getEmployeesUrl));
            if (response.getStatusLine().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                logger.error("api got throttled while getting employees");
                throw new TooManyRequestException();
            }
            return toResponse(response, EmployeesResponse.class).getData();
        } catch (IOException e) {
            logger.error("jsonException occurred fetching all employees", e);
            return new ArrayList<>();
        }

    }

    public List<Employee> getEmployeesByName(String searchString) {
        return getAllEmployees()
                .stream()
                .filter(employee -> employee
                        .getName().toLowerCase()
                        .contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        Optional<Employee> employee = getAllEmployees()
                .stream()
                .filter(e -> e.getId().equalsIgnoreCase(id))
                .findFirst();
        if (!employee.isPresent()) {
            logger.info("employee with id {} does not exists", id);
            throw new EmployeeNotFoundException(id);
        }
        return employee.get();
    }

    public Long getHighestSalaryOfEmployee() {
        Optional<Employee> highestSalaryEmployee = getAllEmployees()
                .stream()
                .max(Comparator.comparingLong(Employee::getSalary));
        if (!highestSalaryEmployee.isPresent()) {
            logger.debug("employee with highest salary not found");
            throw new HighestSalaryNotFoundException();
        }
        return highestSalaryEmployee.get().getSalary();
    }

    public List<String> getTenHighestSalaryEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparingLong(Employee::getSalary).reversed())
                .map(Employee::getName)
                .limit(EMPLOYEE_PAGE_SIZE)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(Map<String, Object> employeeInput) {
        try {
            HttpPost post = toCreateEmployeeRequest(employeeInput);
            CloseableHttpResponse response = closeableHttpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                logger.error("external api got throttled when creating new employee");
                throw new TooManyRequestException();
            }
            CreateEmployeeApiResponse data = toResponse(response, EmployeeCreateResponse.class).getData();
            return new Employee(data.getId(), data.getName(), data.getSalary(), data.getAge(), "");
        } catch (IOException e) {
            logger.trace("failed to execute create employee request", e);
            throw new ApiFailureException(e.getMessage());
        }
    }

    private HttpPost toCreateEmployeeRequest(Map<String, Object> employeeInput) {
        try {
            HttpPost post = new HttpPost(HOSTNAME + API_VERSION + CREATE_EMPLOYEE);
            post.setEntity(new StringEntity(employeeInput.toString()));
            return post;
        } catch (UnsupportedEncodingException e) {
            logger.error("error while parsing create employee request");
            throw new InvalidRequestException("failed to parse employee request");
        }
    }

    public EmployeeDeleteResponse deleteEmployeeById(String id) {
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(
                    new HttpDelete(HOSTNAME + API_VERSION + DELETE_EMPLOYEE + "/" + id)
            );
            if (response.getStatusLine().getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                logger.error("Throttling error while deleting employee with id {}", id);
                throw new TooManyRequestException();
            }
            return toResponse(response, EmployeeDeleteResponse.class);
        } catch (Exception e) {
            logger.debug("error while deleting employee", e);
            throw new ApiFailureException(e.getMessage());
        }
    }

    public <T> T toResponse(CloseableHttpResponse response, Class<T> cls) {
        try {
            String responseEntity = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(responseEntity, cls);
        } catch (IOException e) {
            logger.debug("error while parsing http response", e);
            throw new ApiFailureException(e.getMessage());
        }
    }
}
