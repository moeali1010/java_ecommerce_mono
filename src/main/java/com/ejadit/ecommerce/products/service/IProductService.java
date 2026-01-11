package com.ejadit.ecommerce.products.service;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.products.dto.ProductRequestDto;
import com.ejadit.ecommerce.products.entity.ProductEntity;
import java.util.List;

public interface IProductService {
    ResponseDto<ProductEntity> createProduct(ProductRequestDto requestDto);
    ResponseDto<ProductEntity> getProductById(Long productId);
    ResponseDto<List<ProductEntity>> listProducts();
    ResponseDto<List<ProductEntity>> listProductsByCategory(Long categoryId);
    ResponseDto<ProductEntity> updateProduct(Long productId, ProductRequestDto requestDto);
    ResponseDto<ProductEntity> updateProductStock(Long productId, Integer newQuantity);
}
