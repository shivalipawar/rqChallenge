package com.example.rqchallenge.exception;

public class EmployeeNotFoundException extends RuntimeException{

    public EmployeeNotFoundException(String id){
        super(String.format("Employee with Id %s not found", id));
    }
}
