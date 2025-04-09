package com.ecommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double total;
}
