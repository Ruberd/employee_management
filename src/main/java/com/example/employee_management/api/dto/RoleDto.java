package com.example.employee_management.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDto {
    private String id;
    @NotBlank(message = "Role name should ")
    private String roleName;
    private String roleDescription;
}
