package com.ejadit.ecommerce.orders.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponseDto {

    private Long orderId;
    private Long customerId;
    private Long productId;
    private Integer quantity;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
