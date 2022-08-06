package com.example.rqchallenge.controller;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.models.Employee;
import com.example.rqchallenge.models.EmployeeDeleteResponse;
import com.example.rqchallenge.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EmployeeController implements IEmployeeController {

    public static final String SUCCESS = "success";
    @Autowired
    EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> allEmployees = employeeService.getAllEmployees();
        return ResponseEntity.ok().body(allEmployees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employeesByName = employeeService.getEmployeesByName(searchString);
        return ResponseEntity.ok().body(employeesByName);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employeeById = employeeService.getEmployeeById(id);
        return ResponseEntity.ok().body(employeeById);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Long highestSalaryOfEmployee = employeeService.getHighestSalaryOfEmployee();
        return ResponseEntity.ok().body(highestSalaryOfEmployee.intValue());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> tenHighestSalaryEmployeeNames = employeeService.getTenHighestSalaryEmployeeNames();
        return ResponseEntity.ok().body(tenHighestSalaryEmployeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        Employee employee = employeeService.createEmployee(employeeInput);
        if(employee == null){
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        Employee employeeToBeDeleted = employeeService.getEmployeeById(id);
        EmployeeDeleteResponse employeesResponse = employeeService.deleteEmployeeById(id);
        if(employeesResponse == null || !employeesResponse.getStatus().equalsIgnoreCase(SUCCESS)){
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(employeeToBeDeleted.getName());
    }
}
