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
public class Password {

    private String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Password create(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.password.required");
        }

        if (plainPassword.length() < 6) {
            throw new IllegalArgumentException("validation.password.minlength");
        }

        return new Password(plainPassword);
    }

    public boolean matches(String plainPassword, org.springframework.security.crypto.password.PasswordEncoder encoder) {
        return encoder.matches(plainPassword, this.hashedValue);
    }

    public void setHashedValue(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    @Override
    public String toString() {
        return "****";
    }
}
