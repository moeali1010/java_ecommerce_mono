package com.ejadit.ecommerce.productCategory.mapper;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.productCategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productCategory.dto.ProductCategoryResponseDto;
import com.ejadit.ecommerce.productCategory.entity.ProductCategory;

public class ProductCategoryMapper {

    // ==================== DTO -> ENTITY ====================
    public static ProductCategory toEntity(ProductCategoryRequestDto dto) {
        if (dto == null)
            return null;

        return ProductCategory.create(
            dto.getCategoryName(),
            dto.getDescription()
        );
    }

    // ==================== ENTITY -> RESPONSE DTO ====================
    public static ProductCategoryResponseDto toResponseDto(ProductCategory category) {
        if (category == null)
            return null;

        return ProductCategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .build();
    }

    // ==================== ENTITY -> GENERIC RESPONSE ====================
    public static ResponseDto<ProductCategory> toResponse(ProductCategory category) {
        if (category == null)
            return null;

        return new ResponseDto<>(
                "200",
                "Success",
                category);
    }
}
