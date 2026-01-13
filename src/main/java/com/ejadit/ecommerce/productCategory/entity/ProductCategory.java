package com.ejadit.ecommerce.productcategory.entity;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Product category entity for managing product categorization.
 * 
 * <p>This entity follows the rich domain model pattern with:
 * <ul>
 *   <li>No public setters - state changes only through domain methods</li>
 *   <li>Factory method for controlled creation</li>
 *   <li>update() method for modifying category details</li>
 *   <li>Encapsulated validation logic</li>
 *   <li>Optimistic locking with @Version</li>
 * </ul>
 */
@Entity
@Table(
    name = "product_category",
    indexes = {
        @Index(name = "idx_category_name", columnList = "category_name")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "categoryId")
@ToString
public class ProductCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "category_name", nullable = false, unique = true, length = 255)
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Version
    @Column(name = "version")
    private Long version;

    // ================= FACTORY METHOD =================

    /**
     * Creates a new product category with validation.
     * 
     * @param categoryName the name of the category
     * @param description the category description
     * @return a new ProductCategory instance
     * @throws BusinessException if validation fails
     */
    public static ProductCategory create(String categoryName, String description) {
        ProductCategory category = new ProductCategory();
        category.categoryName = validateName(categoryName);
        category.description = description;
        return category;
    }

    // ================= DOMAIN UPDATE METHOD =================

    /**
     * Updates the category's name and description.
     * 
     * @param categoryName the new category name
     * @param description the new description
     * @throws BusinessException if validation fails
     */
    public void update(String categoryName, String description) {
        this.categoryName = validateName(categoryName);
        this.description = description;
    }

    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("validation.categoryName.required",
                java.util.List.of(FieldErrorDto.builder()
                    .field("categoryName")
                    .message("validation.categoryName.required")
                    .rejectedValue(null)
                    .code("NOT_BLANK")
                    .build()));
        }
        if (name.length() > 255) {
            throw new BusinessException("validation.categoryName.length",
                java.util.List.of(FieldErrorDto.builder()
                    .field("categoryName")
                    .message("validation.categoryName.length")
                    .rejectedValue(name)
                    .code("SIZE_EXCEEDED")
                    .build()));
        }
        return name.trim();
    }
}
