package com.ejadit.ecommerce.auth.service.impl;

import com.ejadit.ecommerce.auth.domain.entity.AuthenticationToken;
import com.ejadit.ecommerce.auth.domain.entity.PasswordReset;
import com.ejadit.ecommerce.auth.domain.entity.ResetStatus;
import com.ejadit.ecommerce.auth.domain.entity.TokenType;
import com.ejadit.ecommerce.auth.domain.valueobject.Email;
import com.ejadit.ecommerce.auth.domain.valueobject.Password;
import com.ejadit.ecommerce.auth.domain.valueobject.Username;
import com.ejadit.ecommerce.auth.dto.AuthResponseDto;
import com.ejadit.ecommerce.auth.dto.ForgotPasswordRequestDto;
import com.ejadit.ecommerce.auth.dto.LoginRequestDto;
import com.ejadit.ecommerce.auth.dto.LoginResponseDto;
import com.ejadit.ecommerce.auth.dto.RegisterRequestDto;
import com.ejadit.ecommerce.auth.dto.ResetPasswordRequestDto;
import com.ejadit.ecommerce.auth.mapper.AuthMapper;
import com.ejadit.ecommerce.auth.repository.AuthenticationTokenRepository;
import com.ejadit.ecommerce.auth.repository.PasswordResetRepository;
import com.ejadit.ecommerce.auth.service.IAuthService;
import com.ejadit.ecommerce.common.dto.FieldErrorDto;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import com.ejadit.ecommerce.common.exception.BusinessException;
import com.ejadit.ecommerce.users.entity.UserEntity;
import com.ejadit.ecommerce.users.entity.UserStatus;
import com.ejadit.ecommerce.users.entity.UserType;
import com.ejadit.ecommerce.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    private static final long ACCESS_TOKEN_EXPIRATION_MINUTES = 60;
    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private static final long PASSWORD_RESET_EXPIRATION_MINUTES = 30;

    private final UserRepository userRepository;
    private final AuthenticationTokenRepository tokenRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    public AuthServiceImpl(UserRepository userRepository,
                         AuthenticationTokenRepository tokenRepository,
                         PasswordResetRepository passwordResetRepository,
                         PasswordEncoder passwordEncoder,
                         AuthMapper authMapper) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
        this.authMapper = authMapper;
    }

    @Override
    @Transactional
    public ResponseDto<AuthResponseDto> register(RegisterRequestDto requestDto) {
        validateRegisterRequest(requestDto);

        if (userRepository.existsByUserName(requestDto.getUsername())) {
            throw new BusinessException("auth.username.exists",
                    List.of(FieldErrorDto.builder()
                            .field("username")
                            .message("auth.username.exists")
                            .rejectedValue(requestDto.getUsername())
                            .code("ALREADY_EXISTS")
                            .build()));
        }

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException("auth.email.exists",
                    List.of(FieldErrorDto.builder()
                            .field("email")
                            .message("auth.email.exists")
                            .rejectedValue(requestDto.getEmail())
                            .code("ALREADY_EXISTS")
                            .build()));
        }

        UserEntity user = UserEntity.create(
                requestDto.getName(),
                requestDto.getUsername().toLowerCase(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getEmail().toLowerCase(),
                requestDto.getMobileNumber(),
                UserType.CUSTOMER,
                UserStatus.ACTIVE
        );

        UserEntity savedUser = userRepository.save(user);
        log.info("User registered successfully: userId={}, username={}", savedUser.getUserId(), savedUser.getUserName());

        return ResponseDto.<AuthResponseDto>builder()
                .statusCode("201")
                .statusMessage("auth.register.success")
                .data(authMapper.toAuthResponseDto(savedUser, "auth.register.success"))
                .build();
    }

    @Override
    @Transactional
    public ResponseDto<LoginResponseDto> login(LoginRequestDto requestDto) {
        UserEntity user = userRepository.findByUserName(requestDto.getUsername())
                .orElseThrow(() -> new BusinessException("auth.login.invalid",
                        List.of(FieldErrorDto.builder()
                                .field("username")
                                .message("auth.login.invalid")
                                .rejectedValue(requestDto.getUsername())
                                .code("NOT_FOUND")
                                .build())));

        if (!user.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new BusinessException("auth.user.inactive",
                    List.of(FieldErrorDto.builder()
                            .field("user")
                            .message("auth.user.inactive")
                            .rejectedValue(null)
                            .code("INACTIVE")
                            .build()));
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", requestDto.getUsername());
            throw new BusinessException("auth.login.invalid",
                    List.of(FieldErrorDto.builder()
                            .field("password")
                            .message("auth.login.invalid")
                            .rejectedValue(null)
                            .code("INVALID")
                            .build()));
        }

        String accessToken = generateToken();
        String refreshToken = generateToken();

        AuthenticationToken accessTokenEntity = AuthenticationToken.create(
                user.getUserId(),
                accessToken,
                TokenType.ACCESS_TOKEN,
                LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRATION_MINUTES)
        );
        tokenRepository.save(accessTokenEntity);

        AuthenticationToken refreshTokenEntity = AuthenticationToken.create(
                user.getUserId(),
                refreshToken,
                TokenType.REFRESH_TOKEN,
                LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS)
        );
        tokenRepository.save(refreshTokenEntity);

        log.info("User logged in successfully: userId={}, username={}", user.getUserId(), user.getUserName());

        return ResponseDto.<LoginResponseDto>builder()
                .statusCode("200")
                .statusMessage("auth.login.success")
                .data(authMapper.toLoginResponseDto(user, accessToken, refreshToken, ACCESS_TOKEN_EXPIRATION_MINUTES * 60))
                .build();
    }

    @Override
    @Transactional
    public ResponseDto<AuthResponseDto> forgotPassword(ForgotPasswordRequestDto requestDto) {
        UserEntity user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new BusinessException("auth.user.notfound",
                        List.of(FieldErrorDto.builder()
                                .field("email")
                                .message("auth.user.notfound")
                                .rejectedValue(requestDto.getEmail())
                                .code("NOT_FOUND")
                                .build())));

        revokePreviousResetTokens(user.getUserId());

        String resetToken = generateToken();
        PasswordReset reset = PasswordReset.create(
                user.getUserId(),
                requestDto.getEmail(),
                resetToken,
                LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRATION_MINUTES)
        );
        passwordResetRepository.save(reset);

        log.info("Password reset token generated for user: userId={}, email={}", user.getUserId(), requestDto.getEmail());

        return ResponseDto.<AuthResponseDto>builder()
                .statusCode("200")
                .statusMessage("auth.password.reset.sent")
                .data(AuthResponseDto.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .message("Check your email for password reset link")
                        .build())
                .build();
    }

    @Override
    @Transactional
    public ResponseDto<AuthResponseDto> resetPassword(ResetPasswordRequestDto requestDto) {
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new BusinessException("auth.password.mismatch",
                    List.of(FieldErrorDto.builder()
                            .field("confirmPassword")
                            .message("auth.password.mismatch")
                            .rejectedValue(null)
                            .code("MISMATCH")
                            .build()));
        }

        PasswordReset reset = passwordResetRepository.findValidResetByToken(requestDto.getToken())
                .orElseThrow(() -> new BusinessException("auth.reset.token.invalid",
                        List.of(FieldErrorDto.builder()
                                .field("token")
                                .message("auth.reset.token.invalid")
                                .rejectedValue(null)
                                .code("INVALID")
                                .build())));

        UserEntity user = userRepository.findById(reset.getUserId())
                .orElseThrow(() -> new BusinessException("auth.user.notfound",
                        List.of(FieldErrorDto.builder()
                                .field("user")
                                .message("auth.user.notfound")
                                .rejectedValue(null)
                                .code("NOT_FOUND")
                                .build())));

        reset.use();
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
        passwordResetRepository.save(reset);

        log.info("Password reset successfully for user: userId={}", user.getUserId());

        return ResponseDto.<AuthResponseDto>builder()
                .statusCode("200")
                .statusMessage("auth.password.reset.success")
                .data(authMapper.toAuthResponseDto(user, "auth.password.reset.success"))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<Boolean> validateToken(String token) {
        try {
            Optional<AuthenticationToken> authToken = tokenRepository.findByToken(token);
            boolean isValid = authToken.isPresent() && authToken.get().isValid();

            return ResponseDto.<Boolean>builder()
                    .statusCode("200")
                    .statusMessage("Token validation completed")
                    .data(isValid)
                    .build();

        } catch (Exception e) {
            log.error("Error validating token", e);
            return ResponseDto.<Boolean>builder()
                    .statusCode("400")
                    .statusMessage("Token validation failed")
                    .data(false)
                    .build();
        }
    }

    @Override
    @Transactional
    public ResponseDto<AuthResponseDto> logout(String token) {
        AuthenticationToken authToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("auth.token.notfound"));

        authToken.revoke();
        tokenRepository.save(authToken);

        log.info("User logged out successfully: userId={}", authToken.getUserId());

        return ResponseDto.<AuthResponseDto>builder()
                .statusCode("200")
                .statusMessage("auth.logout.success")
                .build();
    }

    @Override
    @Transactional
    public ResponseDto<LoginResponseDto> refreshToken(String refreshToken) {
        AuthenticationToken tokenEntity = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("auth.token.notfound"));

        if (!tokenEntity.isValid() || !tokenEntity.getTokenType().equals(TokenType.REFRESH_TOKEN)) {
            throw new BusinessException("auth.refresh.token.invalid");
        }

        UserEntity user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new BusinessException("auth.user.notfound"));

        String newAccessToken = generateToken();
        AuthenticationToken accessTokenEntity = AuthenticationToken.create(
                user.getUserId(),
                newAccessToken,
                TokenType.ACCESS_TOKEN,
                LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRATION_MINUTES)
        );
        tokenRepository.save(accessTokenEntity);

        log.info("Access token refreshed for user: userId={}", user.getUserId());

        return ResponseDto.<LoginResponseDto>builder()
                .statusCode("200")
                .statusMessage("auth.token.refreshed")
                .data(authMapper.toLoginResponseDto(user, newAccessToken, refreshToken, ACCESS_TOKEN_EXPIRATION_MINUTES * 60))
                .build();
    }

    private void validateRegisterRequest(RegisterRequestDto requestDto) {
        try {
            Username.create(requestDto.getUsername());
            Email.create(requestDto.getEmail());
            Password.create(requestDto.getPassword());

            if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
                throw new BusinessException("auth.password.mismatch",
                        List.of(FieldErrorDto.builder()
                                .field("confirmPassword")
                                .message("auth.password.mismatch")
                                .rejectedValue(null)
                                .code("MISMATCH")
                                .build()));
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException("auth.validation.failed",
                    List.of(FieldErrorDto.builder()
                            .field("general")
                            .message(e.getMessage())
                            .rejectedValue(null)
                            .code("INVALID")
                            .build()));
        }
    }

    private void revokePreviousResetTokens(Long userId) {
        List<PasswordReset> previousResets = passwordResetRepository
                .findByExpiresAtBeforeAndStatus(LocalDateTime.now(), ResetStatus.PENDING);
        previousResets.forEach(PasswordReset::revoke);
        passwordResetRepository.saveAll(previousResets);
    }

    private String generateToken() {
        // add some data (userid , username , emil , type , mobile_number and singature) to make token more secure  
        
        return UUID.randomUUID().toString();

    }
}
