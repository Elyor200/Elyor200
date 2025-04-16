package com.ecommerce.dto.payment;

import com.ecommerce.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String paymentType;
    private LocalDateTime paymentDate;
    private PaymentStatus paymentStatus;
}
