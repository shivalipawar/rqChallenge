package com.example.rqchallenge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeApiResponse {

    private String id;
    private String name;
    private Long salary;
    private int age;
}
