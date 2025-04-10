package com.ecommerce.dto.order;

import com.ecommerce.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
    private Double totalPrice;
}
