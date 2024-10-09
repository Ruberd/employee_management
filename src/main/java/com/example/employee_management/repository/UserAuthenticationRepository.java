package com.example.employee_management.repository;

import com.example.employee_management.entity.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, String> {

    @Query("SELECT ua FROM UserAuthentication ua WHERE ua.email = :email")
    Optional<UserAuthentication> findByEmail(String email);
}
