package com.example.rqchallenge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Employee {

    private String id;
    @JsonProperty("employee_name")
    private String name;
    @JsonProperty("employee_salary")
    private Long salary;
    @JsonProperty("employee_age")
    private int age;
    @JsonProperty("profile_image")
    private String imageUrl;
}
