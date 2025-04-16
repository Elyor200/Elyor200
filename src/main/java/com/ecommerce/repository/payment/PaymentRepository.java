package com.ecommerce.repository.payment;

import com.ecommerce.model.Order;
import com.ecommerce.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
}
