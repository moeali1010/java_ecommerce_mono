package com.ejadit.ecommerce.products.entity;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.entity.BaseEntity;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.productcategory.entity.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product entity for managing product information.
 * 
 * <p>This entity follows the rich domain model pattern with:
 * <ul>
 *   <li>No public setters - state changes only through domain methods</li>
 *   <li>Factory method for controlled creation</li>
 *   <li>update() method for complete product updates</li>
 *   <li>updateStock() method for stock-only updates</li>
 *   <li>Encapsulated validation logic with business exceptions</li>
 *   <li>Optimistic locking with @Version</li>
 *   <li>Lazy-loaded category relationship</li>
 * </ul>
 */
@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "product_name"),
                @Index(name = "idx_category_fk", columnList = "category_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "productId")
@ToString(exclude = "category")
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    // ================== RELATION ==================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    // ================== FIELDS ==================
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "price", nullable = false, precision = 18, scale = 4)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Version
    @Column(name = "version")
    private Long version;

    // ================= FACTORY METHOD =================

    /**
     * Creates a new product with validation.
     * 
     * @param category the product category
     * @param productName the product name
     * @param productDescription the product description
     * @param price the product price
     * @param stockQuantity the initial stock quantity
     * @return a new ProductEntity instance
     * @throws BusinessException if any validation fails
     */
    public static ProductEntity create(ProductCategory category,
                                       String productName,
                                       String productDescription,
                                       BigDecimal price,
                                       Integer stockQuantity) {

        ProductEntity product = new ProductEntity();

        product.category = validateCategory(category);
        product.productName = validateProductName(productName);
        product.productDescription = trimDescription(productDescription);
        product.price = validatePrice(price);
        product.stockQuantity = validateStockQuantity(stockQuantity);

        return product;
    }

    // ================= DOMAIN UPDATE METHODS =================

    /**
     * Updates all product fields with validation.
     * 
     * @param category the new product category
     * @param productName the new product name
     * @param productDescription the new product description
     * @param price the new product price
     * @param stockQuantity the new stock quantity
     * @throws BusinessException if any validation fails
     */
    public void update(ProductCategory category,
                       String productName,
                       String productDescription,
                       BigDecimal price,
                       Integer stockQuantity) {

        this.category = validateCategory(category);
        this.productName = validateProductName(productName);
        this.productDescription = trimDescription(productDescription);
        this.price = validatePrice(price);
        this.stockQuantity = validateStockQuantity(stockQuantity);
    }

    /**
     * Updates only the stock quantity.
     * 
     * @param stockQuantity the new stock quantity
     * @throws BusinessException if validation fails
     */
    public void updateStock(Integer stockQuantity) {
        this.stockQuantity = validateStockQuantity(stockQuantity);
    }

    // ================== Validation Methods ==================

    private static ProductCategory validateCategory(ProductCategory category) {
        if (category == null) {
            throw new BusinessException("validation.category.required",
                    List.of(FieldErrorDto.builder()
                            .field("category")
                            .message("validation.category.required")
                            .rejectedValue(null)
                            .code("NOT_NULL")
                            .build()));
        }
        return category;
    }

    private static String validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("validation.productName.required",
                    List.of(FieldErrorDto.builder()
                            .field("productName")
                            .message("validation.productName.required")
                            .rejectedValue(null)
                            .code("NOT_BLANK")
                            .build()));
        }
        if (name.length() > 255) {
            throw new BusinessException("validation.productName.length",
                    List.of(FieldErrorDto.builder()
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

    private static BigDecimal validatePrice(BigDecimal price) {
        if (price == null) {
            throw new BusinessException("validation.price.required",
                    List.of(FieldErrorDto.builder()
                            .field("price")
                            .message("validation.price.required")
                            .rejectedValue(null)
                            .code("NOT_NULL")
                            .build()));
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("validation.price.positive",
                    List.of(FieldErrorDto.builder()
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
                    List.of(FieldErrorDto.builder()
                            .field("stockQuantity")
                            .message("validation.stockQuantity.required")
                            .rejectedValue(null)
                            .code("NOT_NULL")
                            .build()));
        }
        if (stockQuantity < 0) {
            throw new BusinessException("validation.stockQuantity.positive",
                    List.of(FieldErrorDto.builder()
                            .field("stockQuantity")
                            .message("validation.stockQuantity.positive")
                            .rejectedValue(stockQuantity.toString())
                            .code("MUST_BE_POSITIVE_OR_ZERO")
                            .build()));
        }
        return stockQuantity;
    }
}
