package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.entity.CartItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CartItemResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_image_url")
    private String productImageUrl;

    @JsonProperty("product_price")
    private BigDecimal productPrice;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("sub_total")
    private BigDecimal subTotal;

    @JsonProperty("is_product_active")
    private Boolean isProductActive;

    @JsonProperty("is_product_available")
    private Boolean isProductAvailable;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Static method để convert từ Entity sang DTO
    public static CartItemResponse fromEntity(CartItem cartItem) {
        BigDecimal subTotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productImageUrl(cartItem.getProduct().getImageUrl())
                .productPrice(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .subTotal(subTotal)
                .isProductActive(cartItem.getProduct().getIsActive())
                .isProductAvailable(cartItem.getProduct().getQuantity() >= cartItem.getQuantity())
                // .createdAt(cartItem.getCreatedAt())
                // .updatedAt(cartItem.getUpdatedAt())
                .build();
    }
}
