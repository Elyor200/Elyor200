package com.ecommerce.service;

import com.ecommerce.model.User;
import com.ecommerce.model.verification.EmailVerificationToken;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.verification.EmailTokenVerificationRepository;
import com.ecommerce.service.verification.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

//    @Autowired
//    private EmailTokenVerificationRepository emailTokenVerificationRepository;
//
//    @Autowired
//    private EmailService emailService;
//
//    public void register(User user) {
//        user.setIsVerified(false);
//        userRepository.save(user);
//
//        String token = UUID.randomUUID().toString();
//        EmailVerificationToken verificationToken = new EmailVerificationToken();
//        verificationToken.setToken(token);
//        verificationToken.setUser(user);
//        verificationToken.setExpiryTime(LocalDateTime.now().plusMinutes(30));
//        emailTokenVerificationRepository.save(verificationToken);
//
//        emailService.sendConfirmationEmail(user.getEmail(), token);
//    }

}
