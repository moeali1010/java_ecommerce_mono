package com.ejadit.ecommerce.users.dto;
import com.ejadit.ecommerce.users.entity.UserType;
import com.ejadit.ecommerce.users.entity.UserStatus;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String name;
    private String email;
    private String mobileNumber;
    private UserType userType;
    private UserStatus userStatus;

}
