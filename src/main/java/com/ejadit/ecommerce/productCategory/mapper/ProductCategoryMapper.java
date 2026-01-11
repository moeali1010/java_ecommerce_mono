package com.ejadit.ecommerce.productcategory.mapper;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.productcategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productcategory.dto.ProductCategoryResponseDto;
import com.ejadit.ecommerce.productcategory.entity.ProductCategory;

public class ProductCategoryMapper {

    public static ProductCategory toEntity(ProductCategoryRequestDto dto) {
        if (dto == null) return null;
        return ProductCategory.create(dto.getCategoryName(), dto.getDescription());
    }

    public static ProductCategoryResponseDto toResponseDto(ProductCategory entity) {
        if (entity == null) return null;
        return ProductCategoryResponseDto.builder()
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .build();
    }

    public static ResponseDto<ProductCategory> toResponse(ProductCategory entity) {
        if (entity == null) return null;
        return new ResponseDto<>("200", "Success", entity);
    }
}
