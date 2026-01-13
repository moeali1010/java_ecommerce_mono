package com.ejadit.ecommerce.auth.domain.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Password reset entity for managing password reset requests.
 * 
 * <p>This entity follows the rich domain model pattern with:
 * <ul>
 *   <li>No public setters - state changes only through domain methods</li>
 *   <li>Factory method for controlled creation</li>
 *   <li>Domain-specific methods for business operations (use, revoke)</li>
 *   <li>Encapsulated validation logic</li>
 *   <li>State management through ResetStatus enum</li>
 * </ul>
 */
@Entity
@Table(name = "password_resets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_id")
    private Long resetId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResetStatus status = ResetStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    // ================= FACTORY METHOD =================

    /**
     * Creates a new password reset request with validation.
     * 
     * @param userId the user ID requesting password reset
     * @param email the user's email address
     * @param token the reset token
     * @param expiresAt when this reset request expires
     * @return a new PasswordReset instance
     * @throws IllegalArgumentException if any validation fails
     */
    public static PasswordReset create(Long userId, String email, String token, LocalDateTime expiresAt) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("validation.userId.required");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.email.empty");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.token.empty");
        }

        if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("validation.expiration.past");
        }

        PasswordReset reset = new PasswordReset();
        reset.userId = userId;
        reset.email = email;
        reset.token = token;
        reset.status = ResetStatus.PENDING;
        reset.createdAt = LocalDateTime.now();
        reset.expiresAt = expiresAt;

        return reset;
    }

    // ================= DOMAIN BEHAVIOR =================

    /**
     * Marks this password reset as used.
     * 
     * @throws IllegalArgumentException if the token is expired or already used
     */
    public void use() {
        if (isExpired()) {
            throw new IllegalArgumentException("validation.token.expired");
        }

        if (!status.equals(ResetStatus.PENDING)) {
            throw new IllegalArgumentException("validation.token.used");
        }

        this.status = ResetStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * Revokes this password reset request, making it invalid.
     */
    public void revoke() {
        this.status = ResetStatus.REVOKED;
    }

    /**
     * Checks if this password reset request is valid (pending and not expired).
     * 
     * @return true if the request can be used to reset a password
     */
    public boolean isValid() {
        return status.equals(ResetStatus.PENDING) && !isExpired();
    }

    /**
     * Checks if this password reset request has expired.
     * 
     * @return true if the current time is past the expiration time
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
