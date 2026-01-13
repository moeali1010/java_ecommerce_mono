package com.ejadit.ecommerce.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String message;
}
