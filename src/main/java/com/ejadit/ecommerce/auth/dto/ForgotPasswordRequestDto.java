package com.ejadit.ecommerce.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ForgotPasswordRequestDto {

    @JsonCreator
    public ForgotPasswordRequestDto(
            @JsonProperty("email") String email) {
        this.email = email;
    }

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private final String email;
}
