package com.example.employee_management.repository;

import com.example.employee_management.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,String> {
    Users findByEmail(String username);
}
