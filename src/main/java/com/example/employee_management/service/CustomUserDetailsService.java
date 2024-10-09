package com.example.employee_management.service;

import com.example.employee_management.entity.UserAuthentication;
import com.example.employee_management.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthenticationRepository userAuthenticationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAuthentication> userAuthentication = userAuthenticationRepository.findByEmail(username);
        if (userAuthentication.isPresent()) {
            return new org.springframework.security.core.userdetails.User(userAuthentication.get().getEmail(), userAuthentication.get().getPassword(), new ArrayList<>());

        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

    }
}
