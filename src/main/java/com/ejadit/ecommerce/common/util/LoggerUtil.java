package com.ejadit.ecommerce.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Logger Utility Class
 * يوفر طرق موحدة للـ logging عبر التطبيق
 */
@Slf4j
@Component
public class LoggerUtil {

    /**
     * لـ Operations الناجحة
     */
    public static void logSuccess(String message, Object... args) {
        log.info("✓ SUCCESS: {}", String.format(message, args));
    }

    /**
     * لـ Operations الفاشلة
     */
    public static void logError(String message, Exception exception) {
        log.error("✗ ERROR: {}", message, exception);
    }

    /**
     * لـ Database Operations
     */
    public static void logDatabaseOperation(String operation, String tableName, Object id) {
        log.debug("DATABASE: {} Operation on {} table - ID: {}", operation, tableName, id);
    }

    /**
     * لـ API Requests
     */
    public static void logApiRequest(String method, String endpoint, Object payload) {
        log.info("API REQUEST: {} {} - Payload: {}", method, endpoint, payload);
    }

    /**
     * لـ API Responses
     */
    public static void logApiResponse(String endpoint, int status, Object response) {
        log.info("API RESPONSE: {} - Status: {} - Response: {}", endpoint, status, response);
    }

    /**
     * لـ Security Events
     */
    public static void logSecurityEvent(String event, String user) {
        log.warn("SECURITY: {} - User: {}", event, user);
    }

    /**
     * لـ Authentication
     */
    public static void logAuthentication(String username, boolean success) {
        if (success) {
            log.info("AUTH: User '{}' authenticated successfully", username);
        } else {
            log.warn("AUTH: Failed authentication attempt for user '{}'", username);
        }
    }

    /**
     * لـ Performance Metrics
     */
    public static void logPerformance(String operation, long durationMs) {
        if (durationMs > 1000) {
            log.warn("PERFORMANCE: {} took {}ms (Slow Operation)", operation, durationMs);
        } else {
            log.debug("PERFORMANCE: {} took {}ms", operation, durationMs);
        }
    }

    /**
     * لـ Business Logic
     */
    public static void logBusinessLogic(String message, Object... args) {
        log.info("BUSINESS: {}", String.format(message, args));
    }

    /**
     * لـ Warnings
     */
    public static void logWarning(String message, Object... args) {
        log.warn("⚠ WARNING: {}", String.format(message, args));
    }

    /**
     * لـ Debug Information
     */
    public static void logDebug(String message, Object... args) {
        log.debug("DEBUG: {}", String.format(message, args));
    }
}
