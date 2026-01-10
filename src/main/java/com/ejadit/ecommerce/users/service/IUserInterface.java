package com.ejadit.ecommerce.users.service;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.users.dto.UserRequestDto;
import com.ejadit.ecommerce.users.entity.UserEntity;

public interface IUserInterface {
    // create user
    ResponseDto<UserEntity> createUser(UserRequestDto requestDto);

    // get user by id
    ResponseDto<UserEntity> getUserById(Long userId);

    // update user
    ResponseDto<UserEntity> updateUser(Long userId, UserRequestDto requestDto);
}
