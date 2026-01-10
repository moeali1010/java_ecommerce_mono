package com.ejadit.ecommerce.common.exception;

import java.util.List;

import com.ejadit.ecommerce.common.dto.FieldErrorDto;

public class BusinessException extends RuntimeException {

    private final List<FieldErrorDto> fieldErrors;

    public BusinessException(String message) {
        super(message);
        this.fieldErrors = null;
    }

    public BusinessException(String message, List<FieldErrorDto> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public List<FieldErrorDto> getFieldErrors() {
        return fieldErrors;
    }
}
