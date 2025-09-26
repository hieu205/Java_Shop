package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.example.demo.entity.Order;
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
public class OrderResponse {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    private String status;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    private String notes;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("order_items")
    private List<OrderItemResponse> orderItems;

    // convert lay ve thong tin chi tiet don hang
    public static OrderResponse fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseBuilder builder = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes());
        // .createdAt(order.getCreatedAt())
        // .updatedAt(order.getUpdatedAt());

        // Lấy order items nếu có
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemResponse> orderItemResponses = order.getOrderItems()
                    .stream()
                    .map(OrderItemResponse::fromEntity)
                    .collect(Collectors.toList());
            builder.orderItems(orderItemResponses);
        }

        return builder.build();
    }

    // convert tom tat don hang kh can lay chi tiet don hang
    public static OrderResponse fromEntityWithoutItems(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFullName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .shippingAddress(order.getShippingAddress())
                .notes(order.getNotes())
                // .createdAt(order.getCreatedAt())
                // .updatedAt(order.getUpdatedAt())
                .build();
    }
}
