package com.ejadit.ecommerce.users.controller;

import com.ejadit.ecommerce.users.dto.UserRequestDto;
import com.ejadit.ecommerce.users.dto.UserResponseDto;
import com.ejadit.ecommerce.users.entity.UserEntity;
import com.ejadit.ecommerce.users.service.IUserInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.ejadit.ecommerce.users.mapper.UserMapper.toUserResponseDto;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final IUserInterface userService;

    public UserController(IUserInterface userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Validated UserRequestDto userRequestDto) {

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
}
