package com.ejadit.ecommerce.auth.exception;

import com.ejadit.ecommerce.common.exception.BusinessException;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(String messageKey) {
        super(messageKey);
    }

    public AuthenticationException(String messageKey, java.util.List<com.ejadit.ecommerce.common.dto.FieldErrorDto> fieldErrors) {
        super(messageKey, fieldErrors);
    }
}
