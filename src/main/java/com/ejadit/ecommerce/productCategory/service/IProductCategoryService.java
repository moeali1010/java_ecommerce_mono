package com.ejadit.ecommerce.productCategory.service;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.productCategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productCategory.entity.ProductCategory;

public interface IProductCategoryService {
    // Create category
    ResponseDto<ProductCategory> createCategory(ProductCategoryRequestDto requestDto);

    // Get category by id
    ResponseDto<ProductCategory> getCategoryById(Long categoryId);

    // Update category
    ResponseDto<ProductCategory> updateCategory(Long categoryId, ProductCategoryRequestDto requestDto);
}
