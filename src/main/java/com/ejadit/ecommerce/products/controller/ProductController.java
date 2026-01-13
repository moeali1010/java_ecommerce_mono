package com.ejadit.ecommerce.products.controller;

import com.ejadit.ecommerce.products.dto.ProductRequestDto;
import com.ejadit.ecommerce.products.dto.ProductResponseDto;
import com.ejadit.ecommerce.products.entity.ProductEntity;
import com.ejadit.ecommerce.products.mapper.ProductMapper;
import com.ejadit.ecommerce.products.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name = "Products", description = "Product management endpoints - Requires Authentication")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final IProductService service;

    public ProductController(IProductService service) {
        this.service = service;
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    public ResponseEntity<ProductResponseDto> create(@RequestBody @Validated ProductRequestDto request) {
        ProductEntity saved = service.createProduct(request).getData();
        return ResponseEntity.status(201).body(ProductMapper.toProductResponseDto(saved));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable Long productId) {
        ProductEntity product = service.getProductById(productId).getData();
        return ResponseEntity.ok(ProductMapper.toProductResponseDto(product));
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all products")
    @ApiResponse(responseCode = "200", description = "Products listed successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    public ResponseEntity<List<ProductResponseDto>> list() {
        List<ProductEntity> products = service.listProducts().getData();
        List<ProductResponseDto> body = products.stream()
            .map(ProductMapper::toProductResponseDto)
            .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List products by category")
    @ApiResponse(responseCode = "200", description = "Products listed successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    public ResponseEntity<List<ProductResponseDto>> listByCategory(@PathVariable Long categoryId) {
        List<ProductEntity> products = service.listProductsByCategory(categoryId).getData();
        List<ProductResponseDto> body = products.stream()
            .map(ProductMapper::toProductResponseDto)
            .toList();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    public ResponseEntity<ProductResponseDto> update(@PathVariable Long productId,
            @RequestBody @Validated ProductRequestDto request) {
        ProductEntity updated = service.updateProduct(productId, request).getData();
        return ResponseEntity.ok(ProductMapper.toProductResponseDto(updated));
    }

    @PutMapping("/{productId}/stock/{quantity}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update product stock")
    @ApiResponse(responseCode = "200", description = "Product stock updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ProductResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    public ResponseEntity<ProductResponseDto> updateStock(@PathVariable Long productId,
            @PathVariable Integer quantity) {
        ProductEntity updated = service.updateProductStock(productId, quantity).getData();
        return ResponseEntity.ok(ProductMapper.toProductResponseDto(updated));
    }
}
