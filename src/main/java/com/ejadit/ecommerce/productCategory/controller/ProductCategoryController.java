package com.ejadit.ecommerce.productCategory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejadit.ecommerce.productCategory.dto.ProductCategoryRequestDto;
import com.ejadit.ecommerce.productCategory.dto.ProductCategoryResponseDto;
import com.ejadit.ecommerce.productCategory.entity.ProductCategory;
import static com.ejadit.ecommerce.productCategory.mapper.ProductCategoryMapper.toResponseDto;
import com.ejadit.ecommerce.productCategory.service.IProductCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/categories")
@Validated
@Tag(name = "Product Categories", description = "Product category management endpoints")
public class ProductCategoryController {

    private final IProductCategoryService categoryService;

    public ProductCategoryController(IProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("")
    @Operation(
            summary = "Create a new product category",
            description = "Creates a new product category with the provided information. Supports internationalization via Accept-Language header.\n\nSupported Languages:\n- 'ar' for Arabic (العربية)\n- 'en' for English"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Category created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductCategoryResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed or business rule violation"
    )
    public ResponseEntity<ProductCategoryResponseDto> createCategory(
            @RequestBody @Validated ProductCategoryRequestDto requestDto,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String language) {

        // Call service
        var response = categoryService.createCategory(requestDto);

        // Extract entity
        ProductCategory savedCategory = response.getData();

        // Map to response DTO
        ProductCategoryResponseDto body = toResponseDto(savedCategory);

        // Return 201 Created
        return ResponseEntity
                .status(201)
                .body(body);
    }

    @GetMapping("/{categoryId}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a product category by its unique ID. Supports internationalization via Accept-Language header.\n\nSupported Languages:\n- 'ar' for Arabic (العربية)\n- 'en' for English"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Category retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductCategoryResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid category ID"
    )
    public ResponseEntity<ProductCategoryResponseDto> getCategoryById(
            @PathVariable Long categoryId,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String language) {

        // Call service
        var response = categoryService.getCategoryById(categoryId);

        // Extract entity
        ProductCategory category = response.getData();

        // Map to response DTO
        ProductCategoryResponseDto body = toResponseDto(category);

        // Return 200 OK
        return ResponseEntity
                .ok(body);
    }

    @PutMapping("/{categoryId}")
    @Operation(
            summary = "Update category",
            description = "Updates an existing product category with the provided information. Supports internationalization via Accept-Language header.\n\nSupported Languages:\n- 'ar' for Arabic (العربية)\n- 'en' for English"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Category updated successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductCategoryResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed or business rule violation"
    )
    public ResponseEntity<ProductCategoryResponseDto> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Validated ProductCategoryRequestDto requestDto,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String language) {

        // Call service
        var response = categoryService.updateCategory(categoryId, requestDto);

        // Extract entity
        ProductCategory updatedCategory = response.getData();

        // Map to response DTO
        ProductCategoryResponseDto body = toResponseDto(updatedCategory);

        // Return 200 OK
        return ResponseEntity
                .ok(body);
    }
}
