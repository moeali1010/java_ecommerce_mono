package com.ejadit.ecommerce.productCategory.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductCategoryResponseDto {
    private Long categoryId;
    private String categoryName;
    private String description;
}
