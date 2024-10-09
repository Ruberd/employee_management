package com.example.employee_management.service;

import com.example.employee_management.api.dto.RoleDto;
import com.example.employee_management.converter.RoleConverter;
import com.example.employee_management.entity.Roles;
import com.example.employee_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleConverter roleConverter;

    private final RoleRepository roleRepository;

    public String saveRole(RoleDto roleDto){
        Roles roles = roleConverter.convert(roleDto);
        roleRepository.save(roles);

        return "Role saved successfully";
    }
}
