package com.ecommerce.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long chatId;
    private String userName;
    private boolean isVerified;
}
