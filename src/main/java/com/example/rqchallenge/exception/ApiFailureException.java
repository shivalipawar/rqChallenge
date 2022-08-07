package com.example.rqchallenge.exception;

public class ApiFailureException extends RuntimeException {
    public ApiFailureException(String message) {
        super(message);
    }
}
