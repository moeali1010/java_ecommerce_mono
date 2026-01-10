package com.ejadit.ecommerce.users.mapper;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.users.dto.UserRequestDto;
import com.ejadit.ecommerce.users.dto.UserResponseDto;
import com.ejadit.ecommerce.users.entity.UserEntity;

public class UserMapper {

    // ==================== DTO -> ENTITY ====================
    public static UserEntity toEntity(UserRequestDto dto) {
        if (dto == null)
            return null;

        // استخدام Factory method الموجود في UserEntity
        return UserEntity.create(
            dto.getName(),
            dto.getUserName(),
            dto.getPassword(),
            dto.getEmail(),
            dto.getMobileNumber(),
            dto.toUserTypeEnum());
    }

    public static UserResponseDto toUserResponseDto(UserEntity user) {
        if (user == null)
            return null;

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .userType(user.getUserType())
                .build();
    }

    // ==================== ENTITY -> GENERIC RESPONSE ====================
    public static ResponseDto<UserEntity> toResponse(UserEntity user) {
        if (user == null)
            return null;

        return new ResponseDto<>(
                "200",
                "Success",
                user);
    }
}
