package com.ejadit.ecommerce.auth.exception;

import com.ejadit.ecommerce.common.exception.BusinessException;

public class TokenException extends BusinessException {

    public TokenException(String messageKey) {
        super(messageKey);
    }

    public TokenException(String messageKey, java.util.List<com.ejadit.ecommerce.common.dto.FieldErrorDto> fieldErrors) {
        super(messageKey, fieldErrors);
    }
}
