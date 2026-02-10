package com.akuev.controller;

import com.akuev.dto.UserDTO;
import com.akuev.model.User;
import com.akuev.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/internal")
@Tag(name = "Users Response for Microservices")
@SecurityRequirement(name = "bearerAuth")
public class UserResponseController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserResponseController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Найти пользователя по ID")
    public Optional<UserDTO> findUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id).map(this::convertToDto);
    }

    private UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
