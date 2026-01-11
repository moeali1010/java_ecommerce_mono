package com.ejadit.ecommerce.productCategory.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.productCategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productCategory.entity.ProductCategory;
import com.ejadit.ecommerce.productCategory.mapper.ProductCategoryMapper;
import com.ejadit.ecommerce.productCategory.repository.ProductCategoryRepository;
import com.ejadit.ecommerce.productCategory.service.IProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements IProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryServiceImpl(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ResponseDto<ProductCategory> createCategory(ProductCategoryRequestDto requestDto) {
        // 1) Validate category name is not empty
        if (requestDto.getCategoryName() == null || requestDto.getCategoryName().trim().isEmpty()) {
            throw new BusinessException("validation.categoryName.required",
                    List.of(FieldErrorDto.builder()
                            .field("categoryName")
                            .message("validation.categoryName.required")
                            .rejectedValue(null)
                            .code("NOT_BLANK")
                            .build()));
        }

        // 2) Check if category name already exists
        if (categoryRepository.existsByCategoryName(requestDto.getCategoryName())) {
            throw new BusinessException("category.name.exists",
                    List.of(FieldErrorDto.builder()
                            .field("categoryName")
                            .message("category.name.exists")
                            .rejectedValue(requestDto.getCategoryName())
                            .code("ALREADY_EXISTS")
                            .build()));
        }

        // 3) Map DTO → Entity
        ProductCategory category = ProductCategoryMapper.toEntity(requestDto);

        // 4) Save entity
        ProductCategory savedCategory = categoryRepository.save(category);

        // 5) Return standardized response
        return ProductCategoryMapper.toResponse(savedCategory);
    }

    @Override
    public ResponseDto<ProductCategory> getCategoryById(Long categoryId) {
        // Validate categoryId
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException("validation.categoryId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("categoryId")
                            .message("validation.categoryId.invalid")
                            .rejectedValue(categoryId != null ? categoryId.toString() : "null")
                            .code("INVALID_ID")
                            .build()));
        }

        // Find category by id
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("category.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("categoryId")
                                .message("category.not.found")
                                .rejectedValue(categoryId.toString())
                                .code("NOT_FOUND")
                                .build())));

        // Return standardized response
        return ProductCategoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public ResponseDto<ProductCategory> updateCategory(Long categoryId, ProductCategoryRequestDto requestDto) {
        // Validate categoryId
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException("validation.categoryId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("categoryId")
                            .message("validation.categoryId.invalid")
                            .rejectedValue(categoryId != null ? categoryId.toString() : "null")
                            .code("INVALID_ID")
                            .build()));
        }

        // Find existing category
        ProductCategory existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException("category.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("categoryId")
                                .message("category.not.found")
                                .rejectedValue(categoryId.toString())
                                .code("NOT_FOUND")
                                .build())));

        // Validate category name uniqueness (only if changed)
        if (!existingCategory.getCategoryName().equals(requestDto.getCategoryName())) {
            if (categoryRepository.existsByCategoryName(requestDto.getCategoryName())) {
                throw new BusinessException("category.name.exists",
                        List.of(FieldErrorDto.builder()
                                .field("categoryName")
                                .message("category.name.exists")
                                .rejectedValue(requestDto.getCategoryName())
                                .code("ALREADY_EXISTS")
                                .build()));
            }
        }

        // Update category fields
        existingCategory.setCategoryName(requestDto.getCategoryName());
        existingCategory.setDescription(requestDto.getDescription());

        // Save updated category
        ProductCategory updatedCategory = categoryRepository.save(existingCategory);

        // Return standardized response
        return ProductCategoryMapper.toResponse(updatedCategory);
    }
}
