package com.ejadit.ecommerce.products.repository;

import com.ejadit.ecommerce.products.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByProductNameIgnoreCase(String productName);
    Optional<ProductEntity> findByProductNameIgnoreCase(String productName);

    List<ProductEntity> findByCategoryId(Long categoryId);
}
