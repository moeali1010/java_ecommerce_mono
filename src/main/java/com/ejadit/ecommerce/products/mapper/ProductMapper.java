package com.ejadit.ecommerce.products.mapper;

import com.ejadit.ecommerce.products.dto.ProductResponseDto;
import com.ejadit.ecommerce.products.entity.ProductEntity;

public class ProductMapper {

    public static ProductResponseDto toProductResponseDto(ProductEntity entity) {

        Long categoryId = null;
        if (entity.getCategory() != null) {
            categoryId = entity.getCategory().getCategoryId();
        }

        return ProductResponseDto.builder()
            .productId(entity.getProductId())
            .categoryId(categoryId) // from relation
            .productName(entity.getProductName())
            .productDescription(entity.getProductDescription())
            .price(entity.getPrice()) // BigDecimal now
            .stockQuantity(entity.getStockQuantity())
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
