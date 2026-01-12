package com.ejadit.ecommerce.orders.service;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.orders.dto.OrderRequestDto;
import com.ejadit.ecommerce.orders.entity.OrderEntity;
import java.util.List;

public interface IOrderService {
    ResponseDto<OrderEntity> createOrder(OrderRequestDto requestDto);
    ResponseDto<OrderEntity> getOrderById(Long orderId);
    ResponseDto<List<OrderEntity>> listOrders();
    ResponseDto<List<OrderEntity>> listOrdersByCustomer(Long customerId);
    ResponseDto<List<OrderEntity>> listOrdersByProduct(Long productId);
    ResponseDto<OrderEntity> updateOrder(Long orderId, OrderRequestDto requestDto);
    ResponseDto<OrderEntity> updateOrderQuantity(Long orderId, Integer newQuantity);
}
