package com.example.employee_management.repository;

import com.example.employee_management.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles,String> {
}
