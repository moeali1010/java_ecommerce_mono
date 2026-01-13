package com.ejadit.ecommerce.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetPasswordRequestDto {

    @JsonCreator
    public ResetPasswordRequestDto(
            @JsonProperty("token") String token,
            @JsonProperty("newPassword") String newPassword,
            @JsonProperty("confirmPassword") String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    @NotBlank(message = "{validation.token.required}")
    private final String token;

    @NotBlank(message = "{validation.password.required}")
    private final String newPassword;

    @NotBlank(message = "{validation.confirmPassword.required}")
    private final String confirmPassword;
}
