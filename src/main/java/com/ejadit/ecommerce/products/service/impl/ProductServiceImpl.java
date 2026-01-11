package com.ejadit.ecommerce.products.service.impl;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.products.dto.ProductRequestDto;
import com.ejadit.ecommerce.products.entity.ProductEntity;
import com.ejadit.ecommerce.products.repository.ProductRepository;
import com.ejadit.ecommerce.products.service.IProductService;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    // ================== CREATE ==================
    @Override
    @Transactional
    public ResponseDto<ProductEntity> createProduct(ProductRequestDto requestDto) {

        ProductEntity entity = ProductEntity.create(
                requestDto.getCategoryId(),
                requestDto.getProductName(),
                requestDto.getProductDescription(),
                requestDto.getPrice(),
                requestDto.getStockQuantity()
        );

        try {
            ProductEntity saved = repository.save(entity);
            return successResponse(saved, "201", "Created");
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violation (race condition safe)
            if (e.getMessage().toLowerCase().contains("product_name")) {
                throw new BusinessException("product.name.exists",
                    List.of(FieldErrorDto.builder()
                        .field("productName")
                        .message("product.name.exists")
                        .rejectedValue(requestDto.getProductName())
                        .code("UNIQUE_CONSTRAINT")
                        .build()));
            }
            throw e;
        }
    }

    // ================== GET BY ID ==================
    @Override
    public ResponseDto<ProductEntity> getProductById(Long productId) {
        validateProductId(productId);

        ProductEntity product = repository.findById(productId)
            .orElseThrow(() -> new BusinessException("product.not.found",
                List.of(FieldErrorDto.builder()
                    .field("productId")
                    .message("product.not.found")
                    .rejectedValue(String.valueOf(productId))
                    .code("NOT_FOUND")
                    .build())));

        return successResponse(product);
    }

    // ================== LIST ALL ==================
    @Override
    public ResponseDto<List<ProductEntity>> listProducts() {
        List<ProductEntity> products = repository.findAll();
        return successResponse(products);
    }

    // ================== LIST BY CATEGORY ==================
    @Override
    public ResponseDto<List<ProductEntity>> listProductsByCategory(Long categoryId) {
        validateCategoryId(categoryId);
        List<ProductEntity> products = repository.findByCategoryId(categoryId);
        return successResponse(products);
    }

    // ================== UPDATE ==================
    @Override
    @Transactional
    public ResponseDto<ProductEntity> updateProduct(Long productId, ProductRequestDto requestDto) {
        validateProductId(productId);

        ProductEntity existing = repository.findById(productId)
            .orElseThrow(() -> new BusinessException("product.not.found",
                List.of(FieldErrorDto.builder()
                    .field("productId")
                    .message("product.not.found")
                    .rejectedValue(String.valueOf(productId))
                    .code("NOT_FOUND")
                    .build())));

        String newName = requestDto.getProductName();
        if (newName != null && !newName.equalsIgnoreCase(existing.getProductName())) {
            if (repository.existsByProductNameIgnoreCase(newName)) {
                throw new BusinessException("product.name.exists",
                    List.of(FieldErrorDto.builder()
                        .field("productName")
                        .message("product.name.exists")
                        .rejectedValue(newName)
                        .code("UNIQUE_CONSTRAINT")
                        .build()));
            }
        }

        existing.update(
            requestDto.getCategoryId(),
            requestDto.getProductName(),
            requestDto.getProductDescription(),
            requestDto.getPrice(),
            requestDto.getStockQuantity()
        );

        ProductEntity saved = repository.save(existing);
        return successResponse(saved);
    }

    // ================== UPDATE STOCK ONLY ==================
    @Override
    @Transactional
    public ResponseDto<ProductEntity> updateProductStock(Long productId, Integer newQuantity) {
        validateProductId(productId);

        ProductEntity existing = repository.findById(productId)
            .orElseThrow(() -> new BusinessException("product.not.found",
                List.of(FieldErrorDto.builder()
                    .field("productId")
                    .message("product.not.found")
                    .rejectedValue(String.valueOf(productId))
                    .code("NOT_FOUND")
                    .build())));

        existing.updateStock(newQuantity);
        ProductEntity saved = repository.save(existing);
        return successResponse(saved);
    }

    // ================== PRIVATE VALIDATION ==================
    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException("validation.productId.invalid",
                List.of(FieldErrorDto.builder()
                    .field("productId")
                    .message("validation.productId.invalid")
                    .rejectedValue(productId != null ? productId.toString() : null)
                    .code("INVALID_ID")
                    .build()));
        }
    }

    private void validateCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException("validation.categoryId.invalid",
                List.of(FieldErrorDto.builder()
                    .field("categoryId")
                    .message("validation.categoryId.invalid")
                    .rejectedValue(categoryId != null ? categoryId.toString() : null)
                    .code("INVALID_ID")
                    .build()));
        }
    }

    // ================== HELPER RESPONSE ==================
    private <T> ResponseDto<T> successResponse(T data) {
        return successResponse(data, "200", "Success");
    }

    private <T> ResponseDto<T> successResponse(T data, String code, String message) {
        return new ResponseDto<>(code, message, data);
    }
}
