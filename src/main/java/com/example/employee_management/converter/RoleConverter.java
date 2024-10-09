package com.example.employee_management.converter;

import com.example.employee_management.api.dto.RoleDto;
import com.example.employee_management.entity.Roles;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {

    public Roles convert(RoleDto roleDto){
        Roles roles = new Roles();
        roles.setId(roleDto.getId());
        roles.setRoleName(roleDto.getRoleName());
        roles.setRoleDescription(roleDto.getRoleDescription());

        return roles;
    }
}
