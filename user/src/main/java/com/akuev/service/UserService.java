package com.akuev.service;

import com.akuev.model.User;
import com.akuev.repository.UserRepository;
import com.akuev.util.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found!");
        }

        return user;
    }

    @Transactional
    public User createUser(User user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null) {
            throw new RuntimeException("Email and name are required!");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UUID id, User newUser) {
        User user = getUserById(id).get();

        user.setEmail(newUser.getEmail());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id).get();
        userRepository.delete(user);
    }
}
