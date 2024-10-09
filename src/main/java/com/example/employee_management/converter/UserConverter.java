package com.example.employee_management.converter;

import com.example.employee_management.api.dto.UserDto;
import com.example.employee_management.entity.Users;
import com.example.employee_management.enums.UserType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserConverter {

    protected void convert(Users user, UserDto userDto) {
        user.setId(userDto.getId());
        user.setRegisteredDateTime(LocalDate.now());
        user.setEmail(userDto.getEmail());
        user.setUserType(UserType.fromMappedValue(userDto.getUserType()));
        user.setMobile(user.getMobile());
        user.setRoles(userDto.getRoles());
    }

    protected void convert(UserDto userDto,Users user) {
        userDto.setId(user.getId());
        userDto.setRegisteredDateTime(user.getRegisteredDateTime());
        userDto.setEmail(user.getEmail());
        userDto.setUserType(user.getUserType().getMappedValue());
        userDto.setMobile(user.getMobile());
        userDto.setRoles(user.getRoles());
    }
}
