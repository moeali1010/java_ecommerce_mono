package com.ejadit.ecommerce.productCategory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductCategoryRequestDto {

    @JsonCreator
    public ProductCategoryRequestDto(
            @JsonProperty("categoryName") String categoryName,
            @JsonProperty("description") String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    @NotBlank(message = "{validation.categoryName.required}")
    private final String categoryName;

    private final String description;
}
