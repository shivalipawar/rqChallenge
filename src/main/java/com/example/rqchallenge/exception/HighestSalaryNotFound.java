package com.example.rqchallenge.exception;

public class HighestSalaryNotFound extends RuntimeException {
    public HighestSalaryNotFound(){
        super("Employee with highest salary not found");
    }
}
