package com.akuev.controller;

import com.akuev.dto.UserDTO;
import com.akuev.model.User;
import com.akuev.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream().map(this::convertToDto).toList();
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Optional<UserDTO> findUserById(@PathVariable("id") UUID id) {
        UUID userId = getCurrentUserId();
        if (!hasAdminRole() && !id.equals(userId)) {
            throw new SecurityException("Access denied. You can only view your own profile.");
        }

        return userService.getUserById(id).map(this::convertToDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        User user = convertToUser(userDTO);
        User createdUser = userService.createUser(user);
        return convertToDto(createdUser);
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public UserDTO updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        UUID userId = getCurrentUserId();
        if (!hasAdminRole() && !id.equals(userId)) {
            throw new SecurityException("Access denied. You can only view your own profile.");
        }

        User newUser = convertToUser(userDTO);
        User updatedUser = userService.updateUser(id, newUser);
        return convertToDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed({"ADMIN"})
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
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
