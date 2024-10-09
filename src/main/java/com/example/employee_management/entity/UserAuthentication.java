package com.example.employee_management.entity;

import com.example.employee_management.enums.UserType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class UserAuthentication {
    @Id
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @GeneratedValue(generator = "uuid")
    private String id;
    private String email;
    private String password;
    private String userId;
    @Enumerated(EnumType.STRING)
    private UserType userType;
}
