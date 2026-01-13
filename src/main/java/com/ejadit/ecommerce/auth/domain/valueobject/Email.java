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
public class Email {

    private String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email create(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("validation.email.required");
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("validation.email.invalid");
        }

        return new Email(email.toLowerCase().trim());
    }

    @Override
    public String toString() {
        return value;
    }
}
