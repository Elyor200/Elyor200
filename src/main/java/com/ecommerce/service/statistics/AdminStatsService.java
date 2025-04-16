package com.ecommerce.service.statistics;

import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminStatsService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminStatsService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("verifiedUsers", userRepository.countByIsVerified(true));
        stats.put("totalOrders", orderRepository.count());
        return stats;
    }
}
