package com.example.employee_management.api.dto;

import com.example.employee_management.entity.Roles;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String id;
    private String email;
    private String mobile;
    private LocalDate registeredDateTime;
    private String userType;
    private Roles roles;
}
