package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor

public class OrderController {
    @Autowired
    private OrderService orderService;

    // lay tat ca don hang
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // lay don hang theo user
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('CUSTOMER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrderByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderByUserId(id));
    }

    // lay chi tiet don hang theo ID
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // lay chi tiet don hang theo order code
    @GetMapping("/code/{orderCode}")
    public ResponseEntity<OrderResponse> getOrderByCode(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.getOrderByCode(orderCode));
    }

    // lay don hang theo trang thai
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrderBuStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrderByStatus(status));
    }

    // tao don hang tu gio hang
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PostMapping("user/{userId}/from-cart")
    public ResponseEntity<OrderResponse> createOrderByCart(@PathVariable Long userId, @Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrderByCart(userId, order));
    }

    // cap nhat trang thai gio hang
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // huy don hang
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
