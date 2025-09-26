package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.example.demo.entity.Cart;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class CartResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private String username;

    @JsonProperty("cart_items")
    private List<CartItemResponse> cartItems;

    @JsonProperty("total_items")
    private Integer totalItems;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // convert từ Entity sang DTO
    public static CartResponse fromEntity(Cart cart) {
        List<CartItemResponse> cartItemResponses = cart.getCartItems() != null
                ? cart.getCartItems().stream()
                        .map(CartItemResponse::fromEntity)
                        .collect(Collectors.toList())
                : List.of();

        BigDecimal totalAmount = cartItemResponses.stream()
                .map(item -> item.getSubTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = cartItemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .username(cart.getUser().getUsername())
                .cartItems(cartItemResponses)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                // .createdAt(cart.getCreatedAt())
                // .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
