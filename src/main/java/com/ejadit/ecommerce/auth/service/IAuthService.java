package com.ejadit.ecommerce.auth.service;

import com.ejadit.ecommerce.auth.dto.AuthResponseDto;
import com.ejadit.ecommerce.auth.dto.ForgotPasswordRequestDto;
import com.ejadit.ecommerce.auth.dto.LoginRequestDto;
import com.ejadit.ecommerce.auth.dto.LoginResponseDto;
import com.ejadit.ecommerce.auth.dto.RegisterRequestDto;
import com.ejadit.ecommerce.auth.dto.ResetPasswordRequestDto;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.users.entity.UserEntity;

public interface IAuthService {

    /**
     * Register a new user in the system
     */
    ResponseDto<AuthResponseDto> register(RegisterRequestDto requestDto);

    /**
     * Authenticate user and return access token
     */
    ResponseDto<LoginResponseDto> login(LoginRequestDto requestDto);

    /**
     * Request password reset token
     */
    ResponseDto<AuthResponseDto> forgotPassword(ForgotPasswordRequestDto requestDto);

    /**
     * Reset password using valid token
     */
    ResponseDto<AuthResponseDto> resetPassword(ResetPasswordRequestDto requestDto);

    /**
     * Validate authentication token
     */
    ResponseDto<Boolean> validateToken(String token);

    /**
     * Revoke authentication token (logout)
     */
    ResponseDto<AuthResponseDto> logout(String token);

    /**
     * Refresh access token using refresh token
     */
    ResponseDto<LoginResponseDto> refreshToken(String refreshToken);
}
