package com.akuev.service;

import com.akuev.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с пользователями.
 */
public interface UserService {
    /** Возвращает всех пользователей. */
    List<User> getAllUsers();

    /** Находит пользователя по ID. */
    Optional<User> getUserById(UUID id);

    /** Создает нового пользователя. */
    User createUser(User user);

    /** Обновляет данные пользователя. */
    User updateUser(UUID id, User newUser);

    /** Удаляет пользователя по ID. */
    void deleteUser(UUID id);
}