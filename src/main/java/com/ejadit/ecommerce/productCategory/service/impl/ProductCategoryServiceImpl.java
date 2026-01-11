package com.ejadit.ecommerce.productcategory.service.impl;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.productcategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productcategory.entity.ProductCategory;
import com.ejadit.ecommerce.productcategory.mapper.ProductCategoryMapper;
import com.ejadit.ecommerce.productcategory.repository.ProductCategoryRepository;
import com.ejadit.ecommerce.productcategory.service.IProductCategoryService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCategoryServiceImpl implements IProductCategoryService {

    private final ProductCategoryRepository repository;

    public ProductCategoryServiceImpl(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    // ================== CREATE ==================
    @Override
    @Transactional
    public ResponseDto<ProductCategory> createCategory(ProductCategoryRequestDto requestDto) {

        // ------------------- Unique name validation -------------------
        if (repository.existsByCategoryNameIgnoreCase(requestDto.getCategoryName())) {
            throw new BusinessException("category.name.exists",
                List.of(FieldErrorDto.builder()
                    .field("categoryName")
                    .message("category.name.exists")
                    .rejectedValue(requestDto.getCategoryName())
                    .code("UNIQUE_CONSTRAINT")
                    .build()));
        }

        // ------------------- Create entity using Domain factory -------------------
        ProductCategory entity = ProductCategory.create(
                requestDto.getCategoryName(),
                requestDto.getDescription()
        );

        // ------------------- Save -------------------
        ProductCategory saved = repository.save(entity);

        return ProductCategoryMapper.toResponse(saved);
    }

    // ================== GET BY ID ==================
    @Override
    public ResponseDto<ProductCategory> getCategoryById(Long categoryId) {

        validateCategoryId(categoryId);

        ProductCategory category = repository.findById(categoryId)
            .orElseThrow(() -> new BusinessException("category.not.found",
                List.of(FieldErrorDto.builder()
                    .field("categoryId")
                    .message("category.not.found")
                    .rejectedValue(String.valueOf(categoryId))
                    .code("NOT_FOUND")
                    .build())));

        return ProductCategoryMapper.toResponse(category);
    }

    // ================== LIST ==================
    @Override
    public ResponseDto<List<ProductCategory>> listCategories() {
        List<ProductCategory> all = repository.findAll();
        return new ResponseDto<>("200", "Success", all);
    }

    // ================== UPDATE ==================
    @Override
    @Transactional
    public ResponseDto<ProductCategory> updateCategory(Long categoryId, ProductCategoryRequestDto requestDto) {

        validateCategoryId(categoryId);

        ProductCategory existing = repository.findById(categoryId)
            .orElseThrow(() -> new BusinessException("category.not.found",
                List.of(FieldErrorDto.builder()
                    .field("categoryId")
                    .message("category.not.found")
                    .rejectedValue(String.valueOf(categoryId))
                    .code("NOT_FOUND")
                    .build())));

        // ------------------- Unique name check only if changed -------------------
        String newName = requestDto.getCategoryName();
        if (newName != null && !newName.equalsIgnoreCase(existing.getCategoryName())) {
            if (repository.existsByCategoryNameIgnoreCase(newName)) {
                throw new BusinessException("category.name.exists",
                    List.of(FieldErrorDto.builder()
                        .field("categoryName")
                        .message("category.name.exists")
                        .rejectedValue(newName)
                        .code("UNIQUE_CONSTRAINT")
                        .build()));
            }
        }

        // ------------------- Apply domain update -------------------
        existing.update(newName, requestDto.getDescription());

        ProductCategory saved = repository.save(existing);

        return ProductCategoryMapper.toResponse(saved);
    }

    // ================== PRIVATE VALIDATION ==================
    private void validateCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException("validation.categoryId.invalid",
                List.of(FieldErrorDto.builder()
                    .field("categoryId")
                    .message("validation.categoryId.invalid")
                    .rejectedValue(String.valueOf(categoryId))
                    .code("INVALID_ID")
                    .build()));
        }
    }
}
