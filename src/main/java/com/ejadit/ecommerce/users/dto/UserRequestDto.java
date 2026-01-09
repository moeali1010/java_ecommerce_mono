package com.ejadit.ecommerce.users.dto;

import com.ejadit.ecommerce.users.entity.UserType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {

    @JsonCreator
    public UserRequestDto(
            @JsonProperty("name") String name,
            @JsonProperty("userName") String userName,
            @JsonProperty("password") String password,
            @JsonProperty("confirmPassword") String confirmPassword,
            @JsonProperty("email") String email,
            @JsonProperty("userType") UserType userType,
            @JsonProperty("mobileNumber") String mobileNumber) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.userType = userType;
        this.mobileNumber = mobileNumber;
    }

    @NotBlank(message = "{validation.name.required}")
    private final String name;

    @NotBlank(message = "{validation.username.required}")
    private final String userName;

    @NotBlank(message = "{validation.password.required}")
    private final String password;

    @NotBlank(message = "{validation.confirmPassword.required}")
    private final String confirmPassword;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private final String email;

    private final UserType userType;

    @NotBlank(message = "{validation.mobileNumber.required}")
    private final String mobileNumber;
}
