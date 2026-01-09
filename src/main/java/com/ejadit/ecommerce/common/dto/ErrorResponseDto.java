package com.ejadit.ecommerce.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * Standard Error Response DTO for all APIs
 */
@Getter
@Builder
public class ErrorResponseDto {

    private final Instant timestamp;        // timestamp for logs
    private final int status;               // HTTP status code (e.g., 400)
    private final String error;             // HTTP status message (e.g., "Bad Request")
    private final String message;           // detailed error message
    private final String path;              // endpoint that caused the error
    private final List<Map<String, String>> errors; // validation errors list
}
