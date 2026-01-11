package com.ejadit.ecommerce.productcategory.service;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.productcategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productcategory.entity.ProductCategory;
import java.util.List;

public interface IProductCategoryService {
    ResponseDto<ProductCategory> createCategory(ProductCategoryRequestDto requestDto);
    ResponseDto<ProductCategory> getCategoryById(Long categoryId);
    ResponseDto<List<ProductCategory>> listCategories();
    ResponseDto<ProductCategory> updateCategory(Long categoryId, ProductCategoryRequestDto requestDto);
}
