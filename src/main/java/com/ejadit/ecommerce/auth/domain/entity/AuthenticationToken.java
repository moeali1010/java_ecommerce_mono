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
 * Authentication token entity for managing user authentication tokens.
 * 
 * <p>This entity follows the rich domain model pattern with:
 * <ul>
 *   <li>No public setters - state changes only through domain methods</li>
 *   <li>Factory method for controlled creation</li>
 *   <li>Domain-specific methods for business operations</li>
 *   <li>Encapsulated validation logic</li>
 * </ul>
 */
@Entity
@Table(name = "authentication_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthenticationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "token_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ================= FACTORY METHOD =================

    /**
     * Creates a new authentication token with validation.
     * 
     * @param userId the user ID this token belongs to
     * @param token the token string
     * @param tokenType the type of token (ACCESS, REFRESH, etc.)
     * @param expiresAt when this token expires
     * @return a new AuthenticationToken instance
     * @throws IllegalArgumentException if any validation fails
     */
    public static AuthenticationToken create(Long userId, String token, TokenType tokenType, LocalDateTime expiresAt) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("validation.userId.required");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.token.empty");
        }

        if (tokenType == null) {
            throw new IllegalArgumentException("validation.token.type.required");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("validation.expiration.required");
        }

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("validation.expiration.future");
        }

        AuthenticationToken authToken = new AuthenticationToken();
        authToken.userId = userId;
        authToken.token = token;
        authToken.tokenType = tokenType;
        authToken.expiresAt = expiresAt;
        authToken.isRevoked = false;
        authToken.createdAt = LocalDateTime.now();

        return authToken;
    }

    // ================= DOMAIN BEHAVIOR =================

    /**
     * Revokes this authentication token, making it invalid.
     */
    public void revoke() {
        this.isRevoked = true;
    }

    /**
     * Checks if this token is valid (not revoked and not expired).
     * 
     * @return true if the token can be used for authentication
     */
    public boolean isValid() {
        return !isRevoked && LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Checks if this token has expired.
     * 
     * @return true if the current time is past the expiration time
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
