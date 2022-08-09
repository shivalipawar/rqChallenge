package com.example.rqchallenge.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeApiResponse {
    private String id;
    private String name;
    private Long salary;
    private int age;
}
