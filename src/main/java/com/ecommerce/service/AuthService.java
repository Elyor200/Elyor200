package com.ecommerce.service;

import com.ecommerce.dto.AuthResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserResponseDto;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.model.User;
import com.ecommerce.model.verification.EmailVerificationToken;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.verification.EmailTokenVerificationRepository;
import com.ecommerce.service.verification.EmailService;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OtpService otpService;
    private final EmailTokenVerificationRepository emailTokenVerificationRepository;
    private final EmailService emailService;

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder2,
                       UserService userService,
                       OtpService otpService,
                       EmailTokenVerificationRepository emailTokenVerificationRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.otpService = otpService;
        this.emailTokenVerificationRepository = emailTokenVerificationRepository;
        this.emailService = emailService;
    }


    public void register(RegisterRequest registerRequest) {
        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPhoneNumber(registerRequest.getPhoneNumber());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole("ROLE_ADMIN");
            user.setIsVerified(false);
            userRepository.save(user);

            String token = UUID.randomUUID().toString();
            String to = "atabayev.elyor0219@gmail.com";
            emailService.sendConfirmationEmail(to, token, user);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error registering user" + e.getMessage());
        }
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

    public UserResponseDto startAuth(Long chatId, String phoneNumber) {
        User user = userRepository.findByChatId(chatId).orElseGet(() -> createNewUser(chatId, phoneNumber));

        otpService.sendOtp(phoneNumber);
        return mapToDto(user);
    }

    public UserResponseDto verifyOtp(Long chatId, String code) {
        User user = userRepository.findByChatId(chatId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (otpService.validateOtp(user.getPhoneNumber(), code)) {
            user.setIsVerified(true);
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }

        return mapToDto(user);
    }

    private User createNewUser(Long chatId, String phoneNumber) {
        User user = new User();
        user.setChatId(chatId);
        user.setPhoneNumber(phoneNumber);
        user.setIsVerified(false);
        return userRepository.save(user);
    }

    private UserResponseDto mapToDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setChatId(user.getChatId());
        userResponseDto.setVerified(user.getIsVerified());
        userResponseDto.setUserName(user.getUsername());
        return userResponseDto;
    }

}

