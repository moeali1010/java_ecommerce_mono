package com.ejadit.ecommerce.productcategory.controller;

import com.ejadit.ecommerce.productcategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productcategory.dto.ProductCategoryResponseDto;
import com.ejadit.ecommerce.productcategory.entity.ProductCategory;
import com.ejadit.ecommerce.productcategory.mapper.ProductCategoryMapper;
import com.ejadit.ecommerce.productcategory.service.IProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-categories")
@Validated
@Tag(name = "Product Categories", description = "Product category management endpoints")
public class ProductCategoryController {

    private final IProductCategoryService service;

    public ProductCategoryController(IProductCategoryService service) {
        this.service = service;
    }

    @PostMapping("")
    @Operation(summary = "Create a new product category")
    @ApiResponse(responseCode = "201", description = "Category created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductCategoryResponseDto.class)))
    public ResponseEntity<ProductCategoryResponseDto> create(@RequestBody @Validated ProductCategoryRequestDto request) {
        ProductCategory saved = service.createCategory(request).getData();
        return ResponseEntity.status(201).body(ProductCategoryMapper.toResponseDto(saved));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductCategoryResponseDto.class)))
    public ResponseEntity<ProductCategoryResponseDto> getById(@PathVariable Long categoryId) {
        ProductCategory category = service.getCategoryById(categoryId).getData();
        return ResponseEntity.ok(ProductCategoryMapper.toResponseDto(category));
    }

    @GetMapping("")
    @Operation(summary = "List all categories")
    @ApiResponse(responseCode = "200", description = "Categories listed successfully")
    public ResponseEntity<List<ProductCategoryResponseDto>> list() {
        List<ProductCategory> categories = service.listCategories().getData();
        List<ProductCategoryResponseDto> body = categories.stream()
            .map(ProductCategoryMapper::toResponseDto)
            .toList();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update product category")
    @ApiResponse(responseCode = "200", description = "Category updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductCategoryResponseDto.class)))
    public ResponseEntity<ProductCategoryResponseDto> update(@PathVariable Long categoryId,
            @RequestBody @Validated ProductCategoryRequestDto request) {
        ProductCategory updated = service.updateCategory(categoryId, request).getData();
        return ResponseEntity.ok(ProductCategoryMapper.toResponseDto(updated));
    }
}
