package com.example.employee_management.service;

import com.example.employee_management.api.dto.JwtRequest;
import com.example.employee_management.api.dto.JwtResponse;
import com.example.employee_management.api.util.JwtTokenUtil;
import com.example.employee_management.constants.ApplicationConstants;
import com.example.employee_management.entity.Users;
import com.example.employee_management.exception.ServiceException;
import com.example.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import static com.example.employee_management.constants.ApplicationConstants.invalidCredentials;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public Object createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        Users user = userRepository.findByEmail(authenticationRequest.getUsername());

        if (user != null && authenticationRequest.getUserType() != null
                && authenticationRequest.getUserType().equalsIgnoreCase(user.getUserType().getMappedValue())) {

            final String token = jwtTokenUtil.generateToken(user.getEmail(), user);

            return ResponseEntity.ok(new JwtResponse(token));
        }

        return new ResponseEntity<String>(invalidCredentials, HttpStatus.NOT_FOUND);

    }

    private void authenticate(String username, String password) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new ServiceException(invalidCredentials, ApplicationConstants.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }

}
