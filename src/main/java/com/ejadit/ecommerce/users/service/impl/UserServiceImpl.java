package com.ejadit.ecommerce.users.service.impl;

import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.users.dto.UserRequestDto;
import com.ejadit.ecommerce.users.entity.UserEntity;
import com.ejadit.ecommerce.users.entity.UserStatus;
import com.ejadit.ecommerce.users.mapper.UserMapper;
import com.ejadit.ecommerce.users.repository.UserRepository;
import com.ejadit.ecommerce.users.service.IUserInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.ejadit.ecommerce.users.entity.UserType;
import com.ejadit.ecommerce.users.entity.UserStatus;

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
    public ResponseDto<UserEntity> createUser(UserRequestDto requestDto) {

        // 0) Password & confirm password validation before any DB access
        if (requestDto.getPassword() == null || requestDto.getPassword().trim().isEmpty()) {
            throw new BusinessException("validation.password.required",
                    List.of(FieldErrorDto.builder()
                            .field("password")
                            .message("validation.password.required")
                            .rejectedValue(null)
                            .code("NOT_BLANK")
                            .build()));
        }

        if (requestDto.getConfirmPassword() == null || requestDto.getConfirmPassword().trim().isEmpty()) {
            throw new BusinessException("validation.confirmPassword.required",
                    List.of(FieldErrorDto.builder()
                            .field("confirmPassword")
                            .message("validation.confirmPassword.required")
                            .rejectedValue(null)
                            .code("NOT_BLANK")
                            .build()));
        }

        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new BusinessException("user.password.mismatch",
                    List.of(FieldErrorDto.builder()
                            .field("confirmPassword")
                            .message("user.password.mismatch")
                            .rejectedValue(null)
                            .code("PASSWORD_MISMATCH")
                            .build()));
        }

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

        // userType should be ADMIN or CUSTOMER
        UserType userTypeEnum = requestDto.toUserTypeEnum();
        if (userTypeEnum == null) {
            throw new BusinessException("validation.userType.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("userType")
                            .message("validation.userType.invalid")
                            .rejectedValue(requestDto.getUserType())
                            .code("INVALID_ENUM")
                            .build()));
        }

        // Set userStatus to ACTIVE if not provided
        UserStatus userStatusEnum = requestDto.toUserStatusEnum();
        if (userStatusEnum == null) {
            userStatusEnum = UserStatus.ACTIVE; // Default value
        }

        // Proceed to transactional part only after all validations pass
        return doCreateUser(requestDto, userTypeEnum, userStatusEnum);
    }

    @Transactional
    protected ResponseDto<UserEntity> doCreateUser(UserRequestDto requestDto, UserType userTypeEnum,
            UserStatus userStatusEnum) {

        // 5) Map DTO → Entity
        UserEntity userEntity = UserMapper.toEntity(requestDto);

        // 6) Password encryption
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        userEntity.setPassword(encodedPassword);
        userEntity.setUserType(userTypeEnum);
        userEntity.setUserStatus(userStatusEnum);

        // 7) Save entity
        UserEntity savedUser = userRepository.save(userEntity);

        // 8) Return standardized response
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public ResponseDto<UserEntity> getUserById(Long userId) {
        // Validate userId
        if (userId == null || userId <= 0) {
            throw new BusinessException("validation.userId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("userId")
                            .message("validation.userId.invalid")
                            .rejectedValue(userId.toString())
                            .code("INVALID_ID")
                            .build()));
        }

        // Find user by id
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("user.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("userId")
                                .message("user.not.found")
                                .rejectedValue(userId.toString())
                                .code("NOT_FOUND")
                                .build())));

        // Return standardized response
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public ResponseDto<UserEntity> updateUser(Long userId, UserRequestDto requestDto) {
        // Validate userId
        if (userId == null || userId <= 0) {
            throw new BusinessException("validation.userId.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("userId")
                            .message("validation.userId.invalid")
                            .rejectedValue(userId.toString())
                            .code("INVALID_ID")
                            .build()));
        }

        // Find existing user
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("user.not.found",
                        List.of(FieldErrorDto.builder()
                                .field("userId")
                                .message("user.not.found")
                                .rejectedValue(userId.toString())
                                .code("NOT_FOUND")
                                .build())));

        // Validate username uniqueness (only if changed)
        if (!existingUser.getUserName().equals(requestDto.getUserName())) {
            if (userRepository.existsByUserName(requestDto.getUserName())) {
                throw new BusinessException("user.username.exists");
            }
        }

        // Validate email uniqueness (only if changed)
        if (!existingUser.getEmail().equals(requestDto.getEmail())) {
            if (userRepository.existsByEmail(requestDto.getEmail())) {
                throw new BusinessException("user.email.exists");
            }
        }

        // Validate mobile number uniqueness (only if changed)
        if (!existingUser.getMobileNumber().equals(requestDto.getMobileNumber())) {
            if (userRepository.existsByMobileNumber(requestDto.getMobileNumber())) {
                throw new BusinessException("user.mobile.exists");
            }
        }

        // Validate userType
        UserType userTypeEnum = requestDto.toUserTypeEnum();
        if (userTypeEnum == null) {
            throw new BusinessException("validation.userType.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("userType")
                            .message("validation.userType.invalid")
                            .rejectedValue(requestDto.getUserType())
                            .code("INVALID_ENUM")
                            .build()));
        }

        // Validate and update userStatus (optional, but must be valid if provided)
        if (requestDto.getUserStatus() != null && !requestDto.getUserStatus().trim().isEmpty()) {
            UserStatus userStatusEnum = requestDto.toUserStatusEnum();
            if (userStatusEnum == null) {
                throw new BusinessException("validation.userStatus.invalid",
                        List.of(FieldErrorDto.builder()
                                .field("userStatus")
                                .message("validation.userStatus.invalid")
                                .rejectedValue(requestDto.getUserStatus())
                                .code("INVALID_ENUM")
                                .build()));
            }
            existingUser.setUserStatus(userStatusEnum);
        }

        // Update user fields
        existingUser.setUserName(requestDto.getUserName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setMobileNumber(requestDto.getMobileNumber());
        existingUser.setUserType(userTypeEnum);
       

        // Update password only if provided
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            // Validate confirmPassword
            if (requestDto.getConfirmPassword() == null || requestDto.getConfirmPassword().trim().isEmpty()) {
                throw new BusinessException("validation.confirmPassword.required",
                        List.of(FieldErrorDto.builder()
                                .field("confirmPassword")
                                .message("validation.confirmPassword.required")
                                .rejectedValue(null)
                                .code("NOT_BLANK")
                                .build()));
            }

            if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
                throw new BusinessException("user.password.mismatch",
                        List.of(FieldErrorDto.builder()
                                .field("confirmPassword")
                                .message("user.password.mismatch")
                                .rejectedValue(null)
                                .code("PASSWORD_MISMATCH")
                                .build()));
            }

            // Encode and update password
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        // Save updated user
        UserEntity updatedUser = userRepository.save(existingUser);

        // Return standardized response
        return UserMapper.toResponse(updatedUser);
    }
}
