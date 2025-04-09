package com.ecommerce.controller;

import com.ecommerce.dto.cart.CartItemRequest;
import com.ecommerce.dto.cart.CartItemResponse;
import com.ecommerce.model.User;
import com.ecommerce.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequest  cartItemRequest,
                                            @AuthenticationPrincipal User user){
        cartService.addToCart(user.getEmail(), cartItemRequest);
        return ResponseEntity.ok("Item added successfully");
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(cartService.getCartItems(userDetails.getUsername()));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        cartService.removeItem(userDetails.getUsername(), itemId);
        return ResponseEntity.ok("Item removed");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok("Cart cleared");
    }

}
