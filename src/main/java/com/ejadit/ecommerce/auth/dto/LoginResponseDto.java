package com.ejadit.ecommerce.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String email;
    private String tokenType;
    private Long expiresIn;
}
