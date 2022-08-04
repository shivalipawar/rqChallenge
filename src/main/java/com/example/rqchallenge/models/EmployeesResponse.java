package com.example.rqchallenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeesResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    private List<Employee> data;
    @JsonProperty("message")
    private String message;
}
