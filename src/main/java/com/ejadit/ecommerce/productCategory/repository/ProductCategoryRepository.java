package com.ejadit.ecommerce.productCategory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejadit.ecommerce.productCategory.entity.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByCategoryName(String categoryName);

    boolean existsByCategoryName(String categoryName);
}
