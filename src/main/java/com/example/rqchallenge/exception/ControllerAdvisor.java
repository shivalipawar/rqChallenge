package com.example.rqchallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> handleEmployeeNotFoundException(
            EmployeeNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Employee not found");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<Object> handleTooManyRequestException(
            TooManyRequestException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Too many request");

        return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(
            InvalidRequestException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Invalid request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiFailureException.class)
    public ResponseEntity<Object> handleApiFailureException(
            ApiFailureException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Internal server error");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HighestSalaryNotFoundException.class)
    public ResponseEntity<Object> handleHighestSalaryNotFoundException(
            HighestSalaryNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Highest salary of employee not found");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
