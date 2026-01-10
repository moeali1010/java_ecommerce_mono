package com.ejadit.ecommerce.users.dto;

import com.ejadit.ecommerce.users.entity.UserType;
import com.ejadit.ecommerce.users.entity.UserStatus;
import com.ejadit.ecommerce.users.validator.PasswordMatches;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@PasswordMatches
public class UserRequestDto {

    @JsonCreator
    public UserRequestDto(
            @JsonProperty("name") String name,
            @JsonProperty("userName") String userName,
            @JsonProperty("password") String password,
            @JsonProperty("confirmPassword") String confirmPassword,
            @JsonProperty("email") String email,
            @JsonProperty("userType") String userType,
            @JsonProperty("userStatus") String userStatus,
            @JsonProperty("mobileNumber") String mobileNumber) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.userType = userType;
        this.userStatus = userStatus;
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

    @NotBlank(message = "{validation.userType.required}")
    private final String userType;

    // Optional: defaults to ACTIVE if not provided
    private final String userStatus;

    @NotBlank(message = "{validation.mobileNumber.required}")
    private final String mobileNumber;

    public UserType toUserTypeEnum() {
        if (userType == null) {
            return null;
        }
        try {
            return UserType.valueOf(userType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public UserStatus toUserStatusEnum() {
        if (userStatus == null) {
            return null;
        }
        try {
            return UserStatus.valueOf(userStatus.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
