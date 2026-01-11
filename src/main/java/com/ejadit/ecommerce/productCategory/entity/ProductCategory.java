package com.ejadit.ecommerce.productCategory.entity;

import com.ejadit.ecommerce.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "description")
    private String description;

    // ================= FACTORY METHOD =================

    public static ProductCategory create(String categoryName, String description) {
        // Domain validation
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        ProductCategory category = new ProductCategory();
        category.categoryName = categoryName.trim();
        category.description = description != null ? description.trim() : null;

        return category;
    }

    // ================= SETTERS FOR UPDATE =================

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
