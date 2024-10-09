package com.example.employee_management.api.controller;

import com.example.employee_management.api.dto.RoleDto;
import com.example.employee_management.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public String saveRole(@RequestBody @Valid RoleDto roleDto){
        return roleService.saveRole(roleDto);
    }
}
