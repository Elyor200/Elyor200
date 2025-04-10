package com.ecommerce.service;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder passwordEncoder2, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            System.out.println("Login email: " + loginRequest.getEmail());
            System.out.println("Login password: " + loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            System.out.println("Authentication: " + authentication.isAuthenticated());

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("User authenticated: " + userDetails.getUsername());

            String token = jwtUtil.generateToken(userDetails);
            System.out.println("Generated token: " + token);

            return new AuthResponse(token);

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed: " + e.getMessage());
        }
    }

}
