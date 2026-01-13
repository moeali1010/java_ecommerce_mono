package com.ejadit.ecommerce.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequestDto {

    @JsonCreator
    public LoginRequestDto(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    @NotBlank(message = "{validation.username.required}")
    private final String username;

    @NotBlank(message = "{validation.password.required}")
    private final String password;
}
