package com.ecommerce.service.payment;

import com.ecommerce.dto.payment.PaymentRequest;
import com.ecommerce.dto.payment.PaymentResponse;
import com.ecommerce.enums.PaymentStatus;
import com.ecommerce.model.Order;
import com.ecommerce.model.payment.Payment;
import com.ecommerce.repository.order.OrderRepository;
import com.ecommerce.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentResponse pay(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow(() -> new RuntimeException("Order Not Found"));

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new RuntimeException("Payment Already Exists");
        }

        boolean paymentSuccess = Math.random() < 0.9;
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentType(paymentRequest.getPaymentType());
        payment.setPaymentStatus(paymentSuccess ? PaymentStatus.PAID : PaymentStatus.FAILED);

        paymentRepository.save(payment);

        return new PaymentResponse(
                payment.getId(),
                order.getId(),
                payment.getPaymentType(),
                payment.getPaymentDate(),
                payment.getPaymentStatus()
        );
    }

    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrder(
                orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"))
        ).orElseThrow(()  -> new RuntimeException("Payment Not Found"));

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentType(),
                payment.getPaymentDate(),
                payment.getPaymentStatus()
        );
    }
}
