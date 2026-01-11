package com.ejadit.ecommerce.products.dto;

import org.hibernate.validator.constraints.UniqueElements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequestDto {

    @JsonCreator
    public ProductRequestDto(
            @JsonProperty("categoryId") Long categoryId,
            @JsonProperty("productName") String productName,
            @JsonProperty("productDescription") String productDescription,
            @JsonProperty("price") Double price,
            @JsonProperty("stockQuantity") Integer stockQuantity) {
        this.categoryId = categoryId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    @NotNull(message = "{validation.categoryId.required}")
    @Positive(message = "{validation.categoryId.positive}")
    private final Long categoryId;

    @NotBlank(message = "{validation.productName.required}")
    @Size(max = 255, message = "{validation.productName.length}")
    @UniqueElements(message = "{validation.productName.unique}")
    private final String productName;

    private final String productDescription;

    @NotNull(message = "{validation.price.required}")
    @Positive(message = "{validation.price.positive}")
    private final Double price;

    @NotNull(message = "{validation.stockQuantity.required}")
    @PositiveOrZero(message = "{validation.stockQuantity.positive}")
    private final Integer stockQuantity;
}
