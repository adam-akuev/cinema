package com.akuev.repository;

import com.akuev.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностями бронирования.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список бронирований пользователя
     */
    List<Booking> findByUserId(UUID userId);

    /**
     * Находит бронирование по идентификатору бронирования и идентификатору пользователя.
     *
     * @param id идентификатор бронирования
     * @param userId идентификатор пользователя
     * @return Optional с найденным бронированием или пустой, если бронирование не найдено
     */
    Optional<Booking> findByIdAndUserId(Long id, UUID userId);

    /**
     * Проверяет существование бронирования по идентификатору бронирования и идентификатору пользователя.
     *
     * @param id идентификатор бронирования
     * @param userId идентификатор пользователя
     * @return true, если бронирование существует, false - в противном случае
     */
    boolean existsByIdAndUserId(Long id, UUID userId);
}