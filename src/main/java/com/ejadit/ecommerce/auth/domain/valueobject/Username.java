package com.ejadit.ecommerce.auth.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    private String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username create(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.username.required");
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException("validation.username.minlength");
        }

        if (username.length() > 50) {
            throw new IllegalArgumentException("validation.username.maxlength");
        }

        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("validation.username.invalid");
        }

        return new Username(username.toLowerCase().trim());
    }

    @Override
    public String toString() {
        return value;
    }
}
