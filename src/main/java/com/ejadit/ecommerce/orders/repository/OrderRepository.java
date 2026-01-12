package com.ejadit.ecommerce.orders.repository;

import com.ejadit.ecommerce.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomer_UserId(Long customerId);
    List<OrderEntity> findByProduct_ProductId(Long productId);
}
