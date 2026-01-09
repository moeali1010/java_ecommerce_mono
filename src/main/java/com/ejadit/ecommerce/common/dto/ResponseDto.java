package com.ejadit.ecommerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Standard Success Response DTO for all APIs
 * @param <T> type of the actual data payload
 */
@Getter
@Builder
@AllArgsConstructor
public class ResponseDto<T> {
    
    private final String statusCode;      // example: "200"
    private final String statusMessage;   // example: "SUCCESS"
    private final T data;                 // actual payload
}
