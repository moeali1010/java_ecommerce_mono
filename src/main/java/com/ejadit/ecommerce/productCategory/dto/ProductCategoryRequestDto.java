package com.ejadit.ecommerce.productcategory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "{validation.categoryName.length}")
    private final String categoryName;

    private final String description;
}
