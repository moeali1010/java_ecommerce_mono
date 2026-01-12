package com.ejadit.ecommerce.orders.service.impl;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.orders.dto.OrderRequestDto;
import com.ejadit.ecommerce.orders.entity.OrderEntity;
import com.ejadit.ecommerce.orders.repository.OrderRepository;
import com.ejadit.ecommerce.orders.service.IOrderService;
import com.ejadit.ecommerce.products.entity.ProductEntity;
import com.ejadit.ecommerce.products.repository.ProductRepository;
import com.ejadit.ecommerce.users.entity.UserEntity;
import com.ejadit.ecommerce.users.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository repository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository repository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ================== CREATE ==================
    @Override
    @Transactional
    public ResponseDto<OrderEntity> createOrder(OrderRequestDto requestDto) {

        validateCustomerId(requestDto.getCustomerId());
        validateProductId(requestDto.getProductId());

        UserEntity customer = userRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new BusinessException("customer.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("customerId")
                                .message("customer.not.found")
                                .rejectedValue(String.valueOf(requestDto.getCustomerId()))
                                .code("NOT_FOUND")
                                .build())));

        ProductEntity product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new BusinessException("product.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("productId")
                                .message("product.not.found")
                                .rejectedValue(String.valueOf(requestDto.getProductId()))
                                .code("NOT_FOUND")
                                .build())));

        OrderEntity entity = OrderEntity.create(
                customer,
                product,
                requestDto.getQuantity()
        );

        OrderEntity saved = repository.save(entity);
        return successResponse(saved, "201", "Created");
    }

    // ================== GET BY ID ==================
    @Override
    public ResponseDto<OrderEntity> getOrderById(Long orderId) {
        validateOrderId(orderId);

        OrderEntity order = repository.findById(orderId)
                .orElseThrow(() -> new BusinessException("order.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("orderId")
                                .message("order.not.found")
                                .rejectedValue(String.valueOf(orderId))
                                .code("NOT_FOUND")
                                .build())));

        return successResponse(order);
    }

    // ================== LIST ALL ==================
    @Override
    public ResponseDto<List<OrderEntity>> listOrders() {
        List<OrderEntity> orders = repository.findAll();
        return successResponse(orders);
    }

    // ================== LIST BY CUSTOMER ==================
    @Override
    public ResponseDto<List<OrderEntity>> listOrdersByCustomer(Long customerId) {
        validateCustomerId(customerId);

        List<OrderEntity> orders = repository.findByCustomer_UserId(customerId);

        return successResponse(orders);
    }

    // ================== LIST BY PRODUCT ==================
    @Override
    public ResponseDto<List<OrderEntity>> listOrdersByProduct(Long productId) {
        validateProductId(productId);

        List<OrderEntity> orders = repository.findByProduct_ProductId(productId);

        return successResponse(orders);
    }

    // ================== UPDATE ==================
    @Override
    @Transactional
    public ResponseDto<OrderEntity> updateOrder(Long orderId, OrderRequestDto requestDto) {

        validateOrderId(orderId);
        validateCustomerId(requestDto.getCustomerId());
        validateProductId(requestDto.getProductId());

        OrderEntity existing = repository.findById(orderId)
                .orElseThrow(() -> new BusinessException("order.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("orderId")
                                .message("order.not.found")
                                .rejectedValue(String.valueOf(orderId))
                                .code("NOT_FOUND")
                                .build())));

        UserEntity customer = userRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> new BusinessException("customer.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("customerId")
                                .message("customer.not.found")
                                .rejectedValue(String.valueOf(requestDto.getCustomerId()))
                                .code("NOT_FOUND")
                                .build())));

        ProductEntity product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new BusinessException("product.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("productId")
                                .message("product.not.found")
                                .rejectedValue(String.valueOf(requestDto.getProductId()))
                                .code("NOT_FOUND")
                                .build())));

        existing.update(
                customer,
                product,
                requestDto.getQuantity()
        );

        OrderEntity saved = repository.save(existing);
        return successResponse(saved);
    }

    // ================== UPDATE QUANTITY ONLY ==================
    @Override
    @Transactional
    public ResponseDto<OrderEntity> updateOrderQuantity(Long orderId, Integer newQuantity) {

        validateOrderId(orderId);

        OrderEntity existing = repository.findById(orderId)
                .orElseThrow(() -> new BusinessException("order.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("orderId")
                                .message("order.not.found")
                                .rejectedValue(String.valueOf(orderId))
                                .code("NOT_FOUND")
                                .build())));

        existing.updateQuantity(newQuantity);

        OrderEntity saved = repository.save(existing);

        return successResponse(saved);
    }

    // ================== PRIVATE VALIDATION ==================

    private void validateOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("validation.orderId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("orderId")
                            .message("validation.orderId.invalid")
                            .rejectedValue(orderId != null ? orderId.toString() : null)
                            .code("INVALID_ID")
                            .build()));
        }
    }

    private void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new BusinessException("validation.customerId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("customerId")
                            .message("validation.customerId.invalid")
                            .rejectedValue(customerId != null ? customerId.toString() : null)
                            .code("INVALID_ID")
                            .build()));
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException("validation.productId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("productId")
                            .message("validation.productId.invalid")
                            .rejectedValue(productId != null ? productId.toString() : null)
                            .code("INVALID_ID")
                            .build()));
        }
    }

    // ================== SUCCESS RESPONSE HELPERS ==================

    private <T> ResponseDto<T> successResponse(T data) {
        return new ResponseDto<>("200", "OK", data);
    }

    private <T> ResponseDto<T> successResponse(T data, String code, String message) {
        return new ResponseDto<>(code, message, data);
    }
}
