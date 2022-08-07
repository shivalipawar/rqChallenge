package com.example.rqchallenge.exception;

public class HighestSalaryNotFoundException extends RuntimeException {
    public HighestSalaryNotFoundException() {
        super("Employee with highest salary not found");
    }
}
