package com.ejadit.ecommerce.orders.mapper;

import com.ejadit.ecommerce.orders.dto.OrderResponseDto;
import com.ejadit.ecommerce.orders.entity.OrderEntity;

public class OrderMapper {

    public static OrderResponseDto toOrderResponseDto(OrderEntity entity) {

        Long customerId = null;
        if (entity.getCustomer() != null) {
            customerId = entity.getCustomer().getUserId();
        }

        Long productId = null;
        if (entity.getProduct() != null) {
            productId = entity.getProduct().getProductId();
        }

        return OrderResponseDto.builder()
            .orderId(entity.getOrderId())
            .customerId(customerId)
            .productId(productId)
            .quantity(entity.getQuantity())
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
