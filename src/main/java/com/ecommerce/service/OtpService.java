package com.ecommerce.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new HashMap<>();
    public void sendOtp(String phoneNumber){
        String otp = generateOtp(phoneNumber);
        otpStorage.put(phoneNumber, otp);
        System.out.println("OTP Sent to "+phoneNumber);
    }

    public boolean validateOtp(String phoneNumber, String code){
        return otpStorage.getOrDefault(phoneNumber, " ").equals(code);
    }

    private String generateOtp(String phoneNumber){
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }
}
