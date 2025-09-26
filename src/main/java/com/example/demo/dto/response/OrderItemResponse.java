package com.example.demo.dto.response;

import java.math.BigDecimal;

import com.example.demo.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class OrderItemResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_image_url")
    private String productImageUrl;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    public static OrderItemResponse fromEntity(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .productImageUrl(orderItem.getProduct().getImageUrl())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}
