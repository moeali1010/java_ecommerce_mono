package com.ejadit.ecommerce.auth.controller;

import com.ejadit.ecommerce.auth.dto.AuthResponseDto;
import com.ejadit.ecommerce.auth.dto.ForgotPasswordRequestDto;
import com.ejadit.ecommerce.auth.dto.LoginRequestDto;
import com.ejadit.ecommerce.auth.dto.LoginResponseDto;
import com.ejadit.ecommerce.auth.dto.RegisterRequestDto;
import com.ejadit.ecommerce.auth.dto.ResetPasswordRequestDto;
import com.ejadit.ecommerce.auth.service.IAuthService;
import com.ejadit.ecommerce.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication endpoints for register, login, and password management")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<AuthResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto requestDto) {
        log.info("Register request received for username: {}", requestDto.getUsername());
        ResponseDto<AuthResponseDto> response = authService.register(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "User inactive or not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto requestDto) {
        log.info("Login request received for username: {}", requestDto.getUsername());
        ResponseDto<LoginResponseDto> response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    // ================= FORGOT PASSWORD =================

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send password reset token to user email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset email sent successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<AuthResponseDto>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto requestDto) {
        log.info("Forgot password request received for email: {}", requestDto.getEmail());
        ResponseDto<AuthResponseDto> response = authService.forgotPassword(requestDto);
        return ResponseEntity.ok(response);
    }

    // ================= RESET PASSWORD =================

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset user password using valid reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token or password mismatch"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<AuthResponseDto>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto requestDto) {
        log.info("Reset password request received");
        ResponseDto<AuthResponseDto> response = authService.resetPassword(requestDto);
        return ResponseEntity.ok(response);
    }

    // ================= VALIDATE TOKEN =================

    @GetMapping("/validate-token")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Validate authentication token", description = "Check if provided token is valid and not expired")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token validation result",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<Boolean>> validateToken(
            @Parameter(name = "Authorization", description = "Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        log.info("Validate token request received");
        String token = extractTokenFromHeader(authHeader);
        ResponseDto<Boolean> response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    // ================= LOGOUT =================

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout user", description = "Revoke authentication token and logout user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<AuthResponseDto>> logout(
            @Parameter(name = "Authorization", description = "Bearer token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        log.info("Logout request received");
        String token = extractTokenFromHeader(authHeader);
        ResponseDto<AuthResponseDto> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    // ================= REFRESH TOKEN =================

    @PostMapping("/refresh-token")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired refresh token"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto<LoginResponseDto>> refreshToken(
            @Parameter(name = "Authorization", description = "Bearer refresh token", required = true, in = ParameterIn.HEADER)
            @RequestHeader("Authorization") String authHeader) {
        log.info("Refresh token request received");
        String token = extractTokenFromHeader(authHeader);
        ResponseDto<LoginResponseDto> response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }

    // ================= PRIVATE HELPER METHODS =================

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("validation.authorization.header.invalid");
    }
}
