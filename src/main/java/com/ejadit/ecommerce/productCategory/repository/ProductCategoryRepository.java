package com.ejadit.ecommerce.productcategory.repository;

import com.ejadit.ecommerce.productcategory.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    boolean existsByCategoryNameIgnoreCase(String categoryName);
    Optional<ProductCategory> findByCategoryNameIgnoreCase(String categoryName);
}
