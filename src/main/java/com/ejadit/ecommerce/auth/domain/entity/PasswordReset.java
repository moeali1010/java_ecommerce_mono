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

    public static PasswordReset create(Long userId, String email, String token, LocalDateTime expiresAt) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }

        if (expiresAt == null || expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration time must be in the future");
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

    public void use() {
        if (isExpired()) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        if (!status.equals(ResetStatus.PENDING)) {
            throw new IllegalArgumentException("Password reset token is already used");
        }

        this.status = ResetStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void revoke() {
        this.status = ResetStatus.REVOKED;
    }

    public boolean isValid() {
        return status.equals(ResetStatus.PENDING) && !isExpired();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
