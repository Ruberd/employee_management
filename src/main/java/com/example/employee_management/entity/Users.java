package com.example.employee_management.entity;

import com.example.employee_management.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Users {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String email;
    private String mobile;
    private LocalDate registeredDateTime;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @OneToOne(orphanRemoval = false)
    @JoinColumn(name = "roleId")
    private Roles roles;
}
