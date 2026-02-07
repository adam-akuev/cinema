package com.akuev.repository;

import com.akuev.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью User.
 * Предоставляет CRUD операции и дополнительные методы для поиска.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByEmail(String email);
}