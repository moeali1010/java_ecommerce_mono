package com.ejadit.ecommerce.orders.entity;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.entity.BaseEntity;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.products.entity.ProductEntity;
import com.ejadit.ecommerce.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Order entity for managing customer orders.
 * 
 * <p>This entity follows the rich domain model pattern with:
 * <ul>
 *   <li>No public setters - state changes only through domain methods</li>
 *   <li>Factory method for controlled creation</li>
 *   <li>update() method for complete order updates</li>
 *   <li>updateQuantity() method for quantity-only updates</li>
 *   <li>Encapsulated validation logic with business exceptions</li>
 *   <li>Optimistic locking with @Version</li>
 *   <li>Lazy-loaded relationships (customer, product)</li>
 * </ul>
 */
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_customer_fk", columnList = "customer_id"),
                @Index(name = "idx_product_fk", columnList = "product_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "orderId")
@ToString(exclude = {"customer", "product"})
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    // ================== RELATION ==================
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // ================== FIELDS ==================
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Version
    @Column(name = "version")
    private Long version;

    // ================= FACTORY METHOD =================

    /**
     * Creates a new order with validation.
     * 
     * @param customer the customer placing the order
     * @param product the product being ordered
     * @param quantity the order quantity
     * @return a new OrderEntity instance
     * @throws BusinessException if any validation fails
     */
    public static OrderEntity create(UserEntity customer,
                                     ProductEntity product,
                                     Integer quantity) {

        OrderEntity order = new OrderEntity();

        order.customer = validateCustomer(customer);
        order.product = validateProduct(product);
        order.quantity = validateQuantity(quantity);

        return order;
    }

    // ================= DOMAIN UPDATE METHODS =================

    /**
     * Updates all order fields with validation.
     * 
     * @param customer the new customer
     * @param product the new product
     * @param quantity the new quantity
     * @throws BusinessException if any validation fails
     */
    public void update(UserEntity customer,
                       ProductEntity product,
                       Integer quantity) {

        this.customer = validateCustomer(customer);
        this.product = validateProduct(product);
        this.quantity = validateQuantity(quantity);
    }

    /**
     * Updates only the order quantity.
     * 
     * @param quantity the new quantity
     * @throws BusinessException if validation fails
     */
    public void updateQuantity(Integer quantity) {
        this.quantity = validateQuantity(quantity);
    }

    // ================== Validation Methods ==================

    private static UserEntity validateCustomer(UserEntity customer) {
        if (customer == null) {
            throw new BusinessException("validation.customer.required",
                    List.of(FieldErrorDto.builder()
                            .field("customer")
                            .message("validation.customer.required")
                            .rejectedValue(null)
                            .code("NOT_NULL")
                            .build()));
        }
        return customer;
    }

    private static ProductEntity validateProduct(ProductEntity product) {
        if (product == null) {
            throw new BusinessException("validation.product.required",
                    List.of(FieldErrorDto.builder()
                            .field("product")
                            .message("validation.product.required")
                            .rejectedValue(null)
                            .code("NOT_NULL")
                            .build()));
        }
        return product;
    }

    private static Integer validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new BusinessException("validation.quantity.required",
                    List.of(FieldErrorDto.builder()
                            .field("quantity")
                            .message("validation.quantity.required")
                            .rejectedValue(null)
                            .code("NOT_NULL")
                            .build()));
        }
        if (quantity <= 0) {
            throw new BusinessException("validation.quantity.positive",
                    List.of(FieldErrorDto.builder()
                            .field("quantity")
                            .message("validation.quantity.positive")
                            .rejectedValue(quantity.toString())
                            .code("MUST_BE_POSITIVE")
                            .build()));
        }
        return quantity;
    }
}
