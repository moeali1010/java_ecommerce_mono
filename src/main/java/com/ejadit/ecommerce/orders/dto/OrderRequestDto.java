package com.ejadit.ecommerce.orders.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDto {

    @JsonCreator
    public OrderRequestDto(
            @JsonProperty("customerId") Long customerId,
            @JsonProperty("productId") Long productId,
            @JsonProperty("quantity") Integer quantity) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
    }

    @NotNull(message = "{validation.customerId.required}")
    @Positive(message = "{validation.customerId.positive}")
    private final Long customerId;

    @NotNull(message = "{validation.productId.required}")
    @Positive(message = "{validation.productId.positive}")
    private final Long productId;

    @NotNull(message = "{validation.quantity.required}")
    @Positive(message = "{validation.quantity.positive}")
    private final Integer quantity;
}
