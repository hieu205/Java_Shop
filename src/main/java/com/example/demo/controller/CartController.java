package com.example.demo.controller;

import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // lay gio hang cua user
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getUserCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getUserCart(userId));
    }

    // them san pham vao gio hang
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PostMapping("/user/{userId}/items")
    public ResponseEntity<CartResponse> addItemToCart(@PathVariable Long userId,
            @Valid @RequestBody CartItem cartItem) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, cartItem));
    }

    // cap nhat so luong san pham trong gio hang
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PutMapping("/user/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantityCartItem(@PathVariable Long userId, @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantityCartItem(userId, itemId, quantity));
    }

    // xoa san pham khoi gio hang
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER')")
    @DeleteMapping("/user/{userId}/items/{cartItemId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable Long userId, @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, cartItemId));
    }

    // xoa toan bo gio hang
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER')")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}
