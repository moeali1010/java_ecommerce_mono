package com.ejadit.ecommerce.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {

    @JsonCreator
    public RegisterRequestDto(
            @JsonProperty("name") String name,
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("confirmPassword") String confirmPassword,
            @JsonProperty("mobileNumber") String mobileNumber) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.mobileNumber = mobileNumber;
    }

    @NotBlank(message = "{validation.name.required}")
    private final String name;

    @NotBlank(message = "{validation.username.required}")
    private final String username;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private final String email;

    @NotBlank(message = "{validation.password.required}")
    private final String password;

    @NotBlank(message = "{validation.confirmPassword.required}")
    private final String confirmPassword;

    @NotBlank(message = "{validation.mobile.required}")
    private final String mobileNumber;
}
