package com.example.rqchallenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeCreateResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    private Employee data;
    @JsonProperty("message")
    private String message;
}
