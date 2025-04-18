package com.ecommerce.controller.verification;

import com.ecommerce.model.verification.EmailVerificationToken;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.verification.EmailTokenVerificationRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/confirm")
public class ConfirmController {
    private final EmailTokenVerificationRepository emailTokenVerificationRepository;
    private final UserRepository userRepository;

    public ConfirmController(EmailTokenVerificationRepository emailTokenVerificationRepository, UserRepository userRepository) {
        this.emailTokenVerificationRepository = emailTokenVerificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String confirmEmail(@RequestParam("token") String token) {
        EmailVerificationToken verificationToken = emailTokenVerificationRepository.findByToken(token).orElseThrow(() ->
                new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())){
            return "Token is expired";
        }

        var user = verificationToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);

        return "Email verified";
    }
}
