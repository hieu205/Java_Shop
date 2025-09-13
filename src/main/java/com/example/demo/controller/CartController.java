package com.example.demo.controller;

import com.example.demo.entity.Cart;
import com.example.demo.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    // Lấy giỏ hàng theo user
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // Thêm sản phẩm
    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addProductToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addProductToCart(userId, productId, quantity));
    }

    // Xóa sản phẩm
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Cart> removeProductFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeProductFromCart(userId, productId));
    }

    // Xóa toàn bộ giỏ
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
