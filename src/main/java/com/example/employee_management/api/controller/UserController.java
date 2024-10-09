package com.example.employee_management.api.controller;

import com.example.employee_management.api.dto.JwtRequest;
import com.example.employee_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/authenticate")
@RequiredArgsConstructor
public class UserController {
    private final UserService jwtAuthService;

    @PostMapping(value = "/login")
    public Object createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {
        return jwtAuthService.createAuthenticationToken(authenticationRequest);
    }
}
