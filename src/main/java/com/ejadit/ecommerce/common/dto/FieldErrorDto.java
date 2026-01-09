package com.ejadit.ecommerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FieldErrorDto {
    private final String field;
    private final String message;
    private final String rejectedValue;
    private final String code;
}
