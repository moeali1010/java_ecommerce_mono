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

    public static AuthenticationToken create(Long userId, String token, TokenType tokenType, LocalDateTime expiresAt) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }

        if (tokenType == null) {
            throw new IllegalArgumentException("Token type is required");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration time is required");
        }

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expiration must be in the future");
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

    public void revoke() {
        this.isRevoked = true;
    }

    public boolean isValid() {
        return !isRevoked && LocalDateTime.now().isBefore(expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
