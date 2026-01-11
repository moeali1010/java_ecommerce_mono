package com.ejadit.ecommerce.products.mapper;

import com.ejadit.ecommerce.products.dto.ProductResponseDto;
import com.ejadit.ecommerce.products.entity.ProductEntity;

public class ProductMapper {

    public static ProductResponseDto toProductResponseDto(ProductEntity entity) {
        return ProductResponseDto.builder()
            .productId(entity.getProductId())
            .categoryId(entity.getCategoryId())
            .productName(entity.getProductName())
            .productDescription(entity.getProductDescription())
            .price(entity.getPrice())
            .stockQuantity(entity.getStockQuantity())
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
