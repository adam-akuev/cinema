package com.akuev.service.impl;

import com.akuev.model.User;
import com.akuev.repository.UserRepository;
import com.akuev.exception.UserNotFoundException;
import com.akuev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация {@link UserService}.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /** {@inheritDoc} */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found!");
        }

        return user;
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public User createUser(User user) {
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null) {
            throw new RuntimeException("Email and name are required!");
        }
        return userRepository.save(user);
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public User updateUser(UUID id, User newUser) {
        User user = getUserById(id).get();

        user.setEmail(newUser.getEmail());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());

        return userRepository.save(user);
    }

    /** {@inheritDoc} */
    @Transactional
    @Override
    public void deleteUser(UUID id) {
        User user = getUserById(id).get();
        userRepository.delete(user);
    }
}