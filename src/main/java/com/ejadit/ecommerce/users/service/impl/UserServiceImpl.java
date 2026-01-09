package com.ejadit.ecommerce.users.service.impl;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.users.dto.UserRequestDto;
import com.ejadit.ecommerce.users.entity.UserEntity;
import com.ejadit.ecommerce.users.mapper.UserMapper;
import com.ejadit.ecommerce.users.repository.UserRepository;
import com.ejadit.ecommerce.users.service.IUserInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements IUserInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ResponseDto<UserEntity> createUser(UserRequestDto requestDto) {

        // 1) Unique username validation
        if (userRepository.existsByUserName(requestDto.getUserName())) {
            throw new BusinessException("user.username.exists");
        }

        // 2) Unique email validation
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException("user.email.exists");
        }

        // 3) Unique mobile number validation
        if (userRepository.existsByMobileNumber(requestDto.getMobileNumber())) {
            throw new BusinessException("user.mobile.exists");
        }

        // 4) Password & confirm password validation
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new BusinessException("user.password.mismatch");
        }

        // 5) Map DTO → Entity
        UserEntity userEntity = UserMapper.toEntity(requestDto);

        // 6) Password encryption
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        userEntity.setPassword(encodedPassword);

        // 7) Save entity
        UserEntity savedUser = userRepository.save(userEntity);

        // 8) Return standardized response
        return UserMapper.toResponse(savedUser);
    }
}
