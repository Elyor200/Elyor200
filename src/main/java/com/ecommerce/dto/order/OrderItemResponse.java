package com.ecommerce.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponse {
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
}
