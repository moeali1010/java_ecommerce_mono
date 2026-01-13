package com.ejadit.ecommerce.auth.mapper;

import com.ejadit.ecommerce.auth.dto.AuthResponseDto;
import com.ejadit.ecommerce.auth.dto.LoginResponseDto;
import com.ejadit.ecommerce.users.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    default AuthResponseDto toAuthResponseDto(UserEntity userEntity, String message) {
        return AuthResponseDto.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUserName())
                .email(userEntity.getEmail())
                .message(message)
                .build();
    }

    default LoginResponseDto toLoginResponseDto(UserEntity userEntity, String accessToken, String refreshToken, Long expiresIn) {
        return LoginResponseDto.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUserName())
                .email(userEntity.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}
