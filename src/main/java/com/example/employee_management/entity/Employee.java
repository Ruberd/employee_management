package com.example.employee_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@PrimaryKeyJoinColumn(name = "user_id")
@Table(name = "employee")
public class Employee extends Users{
    private String firstName;
    private String lastName;
    private String profile;
    private long currentAgeInDays;
    private LocalDate dateOfBirth;
}
