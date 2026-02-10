package com.akuev.service;

import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.dto.ReserveSeatsRequest;
import com.akuev.model.Booking;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для управления бронированиями билетов на сеансы фильмов.
 */
public interface BookingService {

    /**
     * Создает новое бронирование для пользователя.
     *
     * @param userId идентификатор пользователя
     * @param sessionId идентификатор сеанса
     * @param seatsForBooking набор мест для бронирования
     */
    void create(UUID userId, Long sessionId, Set<String> seatsForBooking);

    /**
     * Получает список всех бронирований.
     *
     * @return список всех бронирований
     */
    List<Booking> getAllBookings();

    /**
     * Находит бронирование по его идентификатору.
     *
     * @param id идентификатор бронирования
     * @return Optional с найденным бронированием или пустой, если бронирование не найдено
     */
    Optional<Booking> getBookingById(Long id);

    /**
     * Находит бронирование по идентификатору бронирования и пользователя.
     *
     * @param id идентификатор бронирования
     * @param userId идентификатор пользователя
     * @return Optional с найденным бронированием или пустой, если бронирование не найдено
     */
    Optional<Booking> getBookingByIdAndUserId(Long id, UUID userId);

    /**
     * Находит все бронирования по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список бронирований пользователя
     */
    List<Booking> findAllByUserId(UUID userId);

    /**
     * Бронирует места на сеансе через сервис фильмов.
     *
     * @param sessionId идентификатор сеанса
     * @param request запрос на бронирование мест
     * @return true, если бронирование успешно, false - в противном случае
     */
    boolean getBookingSeats(Long sessionId, ReserveSeatsRequest request);

    /**
     * Получает информацию о сеансе из сервиса фильмов.
     *
     * @param sessionId идентификатор сеанса
     * @return DTO с информацией о сеансе
     */
    MovieSessionResponseDTO getSession(Long sessionId);

    /**
     * Сохраняет бронирование в базе данных.
     *
     * @param booking бронирование для сохранения
     */
    void saveBooking(Booking booking);

    /**
     * Отменяет бронирование по его идентификатору.
     *
     * @param bookingId идентификатор бронирования для отмены
     */
    void cancelBooking(Long bookingId);

    /**
     * Отменяет бронирование по идентификатору бронирования и пользователя.
     *
     * @param bookingId идентификатор бронирования для отмены
     * @param userId идентификатор пользователя
     */
    void cancelBookingByUserId(Long bookingId, UUID userId);
}