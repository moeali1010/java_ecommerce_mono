package com.ejadit.ecommerce.common.exception;

import com.ejadit.ecommerce.common.dto.ErrorResponseDto;
import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 12);
    }

    // Handle custom business exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        String translatedMessage = messageSource.getMessage(
                ex.getMessage(),
                null,
                ex.getMessage(), // fallback message
                LocaleContextHolder.getLocale()
        );

            List<FieldErrorDto> translatedFieldErrors = null;
            if (ex.getFieldErrors() != null) {
                translatedFieldErrors = ex.getFieldErrors()
                    .stream()
                    .map(err -> FieldErrorDto.builder()
                        .field(err.getField())
                        .message(messageSource.getMessage(
                            err.getMessage(),
                            null,
                            err.getMessage(),
                            LocaleContextHolder.getLocale()))
                        .rejectedValue(err.getRejectedValue())
                        .code(err.getCode())
                        .build())
                    .collect(Collectors.toList());
            }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BUSINESS_ERROR")
                .message(translatedMessage)
                .path(request.getRequestURI())
                .errors(translatedFieldErrors)
                .traceId(generateTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<FieldErrorDto> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> FieldErrorDto.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(String.valueOf(error.getRejectedValue()))
                        .code(convertConstraintNameToCode(error.getCode()))
                        .build()
                )
                .collect(Collectors.toList());

        String validationErrorMessage = messageSource.getMessage(
                "error.validation.failed",
                null,
                "Validation failed",
                LocaleContextHolder.getLocale()
        );

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message(validationErrorMessage)
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .traceId(generateTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle JSON deserialization errors (invalid enum values, type mismatches, etc.)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String errorMessage = messageSource.getMessage(
                "error.invalid.request",
                null,
                "Invalid request body",
                LocaleContextHolder.getLocale()
        );

        // Search through entire exception cause chain
        Throwable cause = ex;
        int depth = 0;
        
        while (cause != null && depth < 10) {
            // Check if this is InvalidFormatException
            if (cause instanceof InvalidFormatException) {
                InvalidFormatException ife = (InvalidFormatException) cause;
                
                if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                    String fieldName = "field";
                    
                    // استخراج اسم الحقل من الـ path
                    if (ife.getPath() != null && !ife.getPath().isEmpty()) {
                        fieldName = ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                    }
                    
                    String value = String.valueOf(ife.getValue());
                    String enumValues = getEnumValues(ife.getTargetType());
                    
                    errorMessage = String.format(
                            "Invalid value '%s' for field '%s'. Accepted values are: %s",
                            value,
                            fieldName,
                            enumValues
                    );
                    break;
                }
            }
            
            cause = cause.getCause();
            depth++;
        }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_REQUEST")
                .message(errorMessage)
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String errorMessage = messageSource.getMessage(
                "error.unexpected",
                null,
                "An unexpected error occurred",
                LocaleContextHolder.getLocale()
        );

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message(errorMessage + ": " + ex.getMessage())
                .path(request.getRequestURI())
                .traceId(generateTraceId())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String convertConstraintNameToCode(String constraintCode) {
        // Convert validation annotation names to codes
        // NotBlank -> NOT_BLANK
        // NotNull -> NOT_NULL
        // Email -> INVALID_EMAIL
        // etc.
        return constraintCode
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }

    private String getEnumValues(Class<?> enumClass) {
        Object[] enumConstants = enumClass.getEnumConstants();
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < enumConstants.length; i++) {
            if (i > 0) {
                values.append(", ");
            }
            values.append(enumConstants[i].toString());
        }
        return values.toString();
    }
}
