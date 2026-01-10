package com.ejadit.ecommerce.users.controller;

import com.ejadit.ecommerce.users.dto.UserRequestDto;
import com.ejadit.ecommerce.users.dto.UserResponseDto;
import com.ejadit.ecommerce.users.entity.UserEntity;
import com.ejadit.ecommerce.users.service.IUserInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.ejadit.ecommerce.users.mapper.UserMapper.toUserResponseDto;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final IUserInterface userService;

    public UserController(IUserInterface userService) {
        this.userService = userService;
    }

    @PostMapping("")
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided information. Supports internationalization via Accept-Language header.\n\nSupported Languages:\n- 'ar' for Arabic (العربية)\n- 'en' for English"
    )
    @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed or business rule violation"
    )
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody @Validated UserRequestDto userRequestDto,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String language) {

        // Call service
        var response = userService.createUser(userRequestDto);

        // Extract entity
        UserEntity savedUser = response.getData();

        // Map to response DTO
        UserResponseDto body = toUserResponseDto(savedUser);

        // Return 201 Created
        return ResponseEntity
                .status(201)
                .body(body);
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their unique ID. Supports internationalization via Accept-Language header.\n\nSupported Languages:\n- 'ar' for Arabic (العربية)\n- 'en' for English"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid user ID"
    )
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long userId,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String language) {

        // Call service
        var response = userService.getUserById(userId);

        // Extract entity
        UserEntity user = response.getData();

        // Map to response DTO
        UserResponseDto body = toUserResponseDto(user);

        // Return 200 OK
        return ResponseEntity
                .ok(body);
    }

    @PutMapping("/{userId}")
    @Operation(
            summary = "Update user",
            description = "Updates an existing user with the provided information. Supports internationalization via Accept-Language header.\n\nSupported Languages:\n- 'ar' for Arabic (العربية)\n- 'en' for English"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "User not found"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed or business rule violation"
    )
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long userId,
            @RequestBody @Validated UserRequestDto userRequestDto,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String language) {

        // Call service
        var response = userService.updateUser(userId, userRequestDto);

        // Extract entity
        UserEntity updatedUser = response.getData();

        // Map to response DTO
        UserResponseDto body = toUserResponseDto(updatedUser);

        // Return 200 OK
        return ResponseEntity
                .ok(body);
    }
}
