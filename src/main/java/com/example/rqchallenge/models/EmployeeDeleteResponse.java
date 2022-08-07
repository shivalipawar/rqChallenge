package com.example.rqchallenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class EmployeeDeleteResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    private String data;
    @JsonProperty("message")
    private String message;
}
