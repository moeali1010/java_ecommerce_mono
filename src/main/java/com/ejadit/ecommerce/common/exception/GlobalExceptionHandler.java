package com.ejadit.ecommerce.common.exception;

import com.ejadit.ecommerce.common.dto.ErrorResponseDto;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
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

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(translatedMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage(),
                        "rejectedValue", String.valueOf(error.getRejectedValue())
                ))
                .collect(Collectors.toList());

        String validationErrorTitle = messageSource.getMessage(
                "error.validation",
                null,
                "Validation Error",
                LocaleContextHolder.getLocale()
        );

        String validationErrorMessage = messageSource.getMessage(
                "error.validation.failed",
                null,
                "Validation failed",
                LocaleContextHolder.getLocale()
        );

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(validationErrorTitle)
                .message(validationErrorMessage)
                .path(request.getRequestURI())
                .errors(fieldErrors)
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
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(errorMessage + ": " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
