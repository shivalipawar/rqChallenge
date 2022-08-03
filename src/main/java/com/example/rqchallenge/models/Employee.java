package com.example.rqchallenge.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Employee {

    String name;
    Long salary;
    int age;
    String imageUrl;
}
