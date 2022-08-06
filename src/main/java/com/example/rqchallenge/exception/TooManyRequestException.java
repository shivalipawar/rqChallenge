package com.example.rqchallenge.exception;

public class TooManyRequestException extends RuntimeException {

    public TooManyRequestException(){
        super("Too many requests");
    }
}
