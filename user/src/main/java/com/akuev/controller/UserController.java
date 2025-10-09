package com.akuev.controller;

import com.akuev.dto.UserDTO;
import com.akuev.model.User;
import com.akuev.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream().map(this::convertToDto).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<UserDTO> findUserById(@PathVariable("id") UUID id) {
        UUID userId = getCurrentUserId();
        if (!hasAdminRole() && !id.equals(userId)) {
            throw new SecurityException("Access denied. You can only view your own profile.");
        }

        Optional<UserDTO> user = userService.getUserById(id).map(this::convertToDto);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = convertToUser(userDTO);
        User createdUser = userService.createUser(user);
        UserDTO createdUserDTO = convertToDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDTO);
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        UUID userId = getCurrentUserId();
        if (!hasAdminRole() && !id.equals(userId)) {
            throw new SecurityException("Access denied. You can only view your own profile.");
        }

        User newUser = convertToUser(userDTO);
        User updatedUser = userService.updateUser(id, newUser);
        UserDTO updatedUserDTO = convertToDto(updatedUser);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getSubject();
            return UUID.fromString(userId);
        }
        throw new SecurityException("User not authenticated!");
    }

    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    private UserDTO convertToDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}