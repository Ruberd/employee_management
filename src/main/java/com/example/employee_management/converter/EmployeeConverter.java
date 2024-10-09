package com.example.employee_management.converter;

import com.example.employee_management.api.dto.EmployeeDto;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.entity.Roles;
import com.example.employee_management.exception.ServiceException;
import com.example.employee_management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class EmployeeConverter extends UserConverter {

    private final RoleRepository roleRepository;

    public Employee convert(EmployeeDto employeeDto){
        Employee employee = new Employee();
        super.convert(employee,employeeDto);
        employee.setId(employeeDto.getId());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setProfile(employeeDto.getProfile());
        employee.setMobile(employeeDto.getMobile());
        if (employeeDto.getRoles() != null) {
            Roles existingRole = roleRepository.findById(employeeDto.getRoles().getId())
                    .orElseThrow(() -> new ServiceException("Role not found","Bad request", HttpStatus.BAD_REQUEST));
            employee.setRoles(existingRole);
        }

        if (employeeDto.getDateOfBirth() != null) {
            long days = ChronoUnit.DAYS.between(employeeDto.getDateOfBirth(), LocalDate.now());
            employee.setCurrentAgeInDays(days);
            employee.setDateOfBirth(employeeDto.getDateOfBirth());
        }
        return employee;
    }

    public EmployeeDto convertToDto(Employee employee){
        EmployeeDto employeeDto = new EmployeeDto();
        super.convert(employeeDto,employee);
        employeeDto.setId(employee.getId());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setProfile(employee.getProfile());
        employeeDto.setDateOfBirth(employee.getDateOfBirth());
        return employeeDto;
    }

    public void convertForUpdate(Employee existingEmployee, EmployeeDto newEmployee){
        super.convert(existingEmployee,newEmployee);
        existingEmployee.setId(newEmployee.getId());
        existingEmployee.setFirstName(newEmployee.getFirstName());
        existingEmployee.setLastName(newEmployee.getLastName());
        existingEmployee.setProfile(newEmployee.getProfile());
        existingEmployee.setMobile(newEmployee.getMobile());
        if (newEmployee.getRoles() != null) {
            Roles existingRole = roleRepository.findById(newEmployee.getRoles().getId())
                    .orElseThrow(() -> new ServiceException("Role not found","Bad request", HttpStatus.BAD_REQUEST));
            existingEmployee.setRoles(existingRole);
        }
    }
}
