package com.ejadit.ecommerce.products.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponseDto {

    private Long productId;
    private Long categoryId;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private Integer stockQuantity;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
