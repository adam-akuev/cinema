package com.akuev.controller;

import com.akuev.converter.UserMapper;
import com.akuev.dto.UserDTO;
import com.akuev.model.User;
import com.akuev.service.SecurityService;
import com.akuev.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityService securityService;

    @GetMapping
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Найти пользователя по ID")
    public ResponseEntity<UserDTO> findUserById(@PathVariable("id") UUID id) {
        UUID userId = securityService.getCurrentUserId();
        boolean isAdmin = securityService.hasAdminRole();
        if (!isAdmin && !id.equals(userId)) {
            throw new SecurityException("Access denied. You can only view your own profile.");
        }

        Optional<UserDTO> user = userService.getUserById(id).map(userMapper::toDto);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать пользователя")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User createdUser = userService.createUser(user);
        UserDTO createdUserDTO = userMapper.toDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDTO);
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Обновить пользователя")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        UUID userId = securityService.getCurrentUserId();
        boolean isAdmin = securityService.hasAdminRole();
        if (!isAdmin && !id.equals(userId)) {
            throw new SecurityException("Access denied. You can only view your own profile.");
        }

        User newUser = userMapper.toEntity(userDTO);
        User updatedUser = userService.updateUser(id, newUser);
        UserDTO updatedUserDTO = userMapper.toDto(updatedUser);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Удалить пользователя")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}