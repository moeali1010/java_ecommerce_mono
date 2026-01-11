package com.ejadit.ecommerce.products.entity;

import com.ejadit.ecommerce.common.entity.BaseEntity;
import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "product_name"),
        @Index(name = "idx_category_id", columnList = "category_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "productId")
@ToString
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Version
    @Column(name = "version")
    private Long version;

    // ================== Factory Method ==================
    public static ProductEntity create(Long categoryId,
                                       String productName,
                                       String productDescription,
                                       Double price,
                                       Integer stockQuantity) {
        ProductEntity product = new ProductEntity();
        product.categoryId = validateCategoryId(categoryId);
        product.productName = validateProductName(productName);
        product.productDescription = trimDescription(productDescription);
        product.price = validatePrice(price);
        product.stockQuantity = validateStockQuantity(stockQuantity);
        return product;
    }

    // ================== Update Method ==================
    public void update(Long categoryId,
                       String productName,
                       String productDescription,
                       Double price,
                       Integer stockQuantity) {
        this.categoryId = validateCategoryId(categoryId);
        this.productName = validateProductName(productName);
        this.productDescription = trimDescription(productDescription);
        this.price = validatePrice(price);
        this.stockQuantity = validateStockQuantity(stockQuantity);
    }

    // ================== Update stock only ==================
    public void updateStock(Integer stockQuantity) {
        this.stockQuantity = validateStockQuantity(stockQuantity);
    }

    // ================== Validation Methods ==================
    private static Long validateCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException("validation.categoryId.invalid",
                java.util.List.of(FieldErrorDto.builder()
                    .field("categoryId")
                    .message("validation.categoryId.invalid")
                    .rejectedValue(categoryId != null ? categoryId.toString() : null)
                    .code("INVALID_ID")
                    .build()));
        }
        return categoryId;
    }

    private static String validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("validation.productName.required",
                java.util.List.of(FieldErrorDto.builder()
                    .field("productName")
                    .message("validation.productName.required")
                    .rejectedValue(null)
                    .code("NOT_BLANK")
                    .build()));
        }
        if (name.length() > 255) {
            throw new BusinessException("validation.productName.length",
                java.util.List.of(FieldErrorDto.builder()
                    .field("productName")
                    .message("validation.productName.length")
                    .rejectedValue(name)
                    .code("SIZE_EXCEEDED")
                    .build()));
        }
        return name.trim();
    }

    private static String trimDescription(String description) {
        return description != null ? description.trim() : null;
    }

    private static Double validatePrice(Double price) {
        if (price == null) {
            throw new BusinessException("validation.price.required",
                java.util.List.of(FieldErrorDto.builder()
                    .field("price")
                    .message("validation.price.required")
                    .rejectedValue(null)
                    .code("NOT_NULL")
                    .build()));
        }
        if (price <= 0) {
            throw new BusinessException("validation.price.positive",
                java.util.List.of(FieldErrorDto.builder()
                    .field("price")
                    .message("validation.price.positive")
                    .rejectedValue(price.toString())
                    .code("MUST_BE_POSITIVE")
                    .build()));
        }
        return price;
    }

    private static Integer validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null) {
            throw new BusinessException("validation.stockQuantity.required",
                java.util.List.of(FieldErrorDto.builder()
                    .field("stockQuantity")
                    .message("validation.stockQuantity.required")
                    .rejectedValue(null)
                    .code("NOT_NULL")
                    .build()));
        }
        if (stockQuantity < 0) {
            throw new BusinessException("validation.stockQuantity.positive",
                java.util.List.of(FieldErrorDto.builder()
                    .field("stockQuantity")
                    .message("validation.stockQuantity.positive")
                    .rejectedValue(stockQuantity.toString())
                    .code("MUST_BE_POSITIVE_OR_ZERO")
                    .build()));
        }
        return stockQuantity;
    }
}
