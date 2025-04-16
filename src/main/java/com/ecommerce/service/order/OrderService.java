package com.ecommerce.service.order;

import com.ecommerce.dto.order.OrderItemResponse;
import com.ecommerce.dto.order.OrderResponse;
import com.ecommerce.model.*;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    public OrderResponse placeOrder(String userEmail){
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(item.getProduct().getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItem.setOrder(order);

            total += item.getProduct().getPrice() * item.getQuantity();
            orderItems.add(orderItem);


            product.setStock(product.getStock()-item.getQuantity());
            productRepository.save(product);
        }


        order.setItems(orderItems);
        order.setTotalPrice(total);


        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);


        return mapToResponse(order);
    }

    public List<OrderResponse> getUserOrders(String userEmail){
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<OrderResponse> getAllOrders(){
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order){
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductName(),
                        i.getPrice(),
                        i.getQuantity(),
                        i.getPrice() * i.getQuantity()
                ))
                .toList();

        return new OrderResponse(order.getId(), order.getOrderDate(), itemResponses, order.getTotalPrice());
    }

}
