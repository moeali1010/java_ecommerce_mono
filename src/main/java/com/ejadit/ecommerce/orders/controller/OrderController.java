package com.ejadit.ecommerce.orders.controller;

import com.ejadit.ecommerce.orders.dto.OrderRequestDto;
import com.ejadit.ecommerce.orders.dto.OrderResponseDto;
import com.ejadit.ecommerce.orders.entity.OrderEntity;
import com.ejadit.ecommerce.orders.mapper.OrderMapper;
import com.ejadit.ecommerce.orders.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Validated
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final IOrderService service;

    public OrderController(IOrderService service) {
        this.service = service;
    }

    @PostMapping("")
    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = OrderResponseDto.class)))
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Validated OrderRequestDto request) {
        OrderEntity saved = service.createOrder(request).getData();
        return ResponseEntity.status(201).body(OrderMapper.toOrderResponseDto(saved));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = OrderResponseDto.class)))
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long orderId) {
        OrderEntity order = service.getOrderById(orderId).getData();
        return ResponseEntity.ok(OrderMapper.toOrderResponseDto(order));
    }

    @GetMapping("")
    @Operation(summary = "List all orders")
    @ApiResponse(responseCode = "200", description = "Orders listed successfully")
    public ResponseEntity<List<OrderResponseDto>> list() {
        List<OrderEntity> orders = service.listOrders().getData();
        List<OrderResponseDto> body = orders.stream()
            .map(OrderMapper::toOrderResponseDto)
            .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "List orders by customer")
    @ApiResponse(responseCode = "200", description = "Orders listed successfully")
    public ResponseEntity<List<OrderResponseDto>> listByCustomer(@PathVariable Long customerId) {
        List<OrderEntity> orders = service.listOrdersByCustomer(customerId).getData();
        List<OrderResponseDto> body = orders.stream()
            .map(OrderMapper::toOrderResponseDto)
            .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "List orders by product")
    @ApiResponse(responseCode = "200", description = "Orders listed successfully")
    public ResponseEntity<List<OrderResponseDto>> listByProduct(@PathVariable Long productId) {
        List<OrderEntity> orders = service.listOrdersByProduct(productId).getData();
        List<OrderResponseDto> body = orders.stream()
            .map(OrderMapper::toOrderResponseDto)
            .toList();
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order")
    @ApiResponse(responseCode = "200", description = "Order updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = OrderResponseDto.class)))
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long orderId,
            @RequestBody @Validated OrderRequestDto request) {
        OrderEntity updated = service.updateOrder(orderId, request).getData();
        return ResponseEntity.ok(OrderMapper.toOrderResponseDto(updated));
    }

    @PutMapping("/{orderId}/quantity/{quantity}")
    @Operation(summary = "Update order quantity")
    @ApiResponse(responseCode = "200", description = "Order quantity updated successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = OrderResponseDto.class)))
    public ResponseEntity<OrderResponseDto> updateQuantity(@PathVariable Long orderId,
            @PathVariable Integer quantity) {
        OrderEntity updated = service.updateOrderQuantity(orderId, quantity).getData();
        return ResponseEntity.ok(OrderMapper.toOrderResponseDto(updated));
    }
}
