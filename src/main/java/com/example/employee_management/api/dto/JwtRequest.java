package com.example.employee_management.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
public class JwtRequest {
    @NotNull(message = "Username cannot be null")
    @Email(message = "Username should be in correct format")
    private String username;
    @NotNull(message = "Password cannot be null")
    private String password;
    @NotNull(message = "Password cannot be null")
    private String userType;
}
