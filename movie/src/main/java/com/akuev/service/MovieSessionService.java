package com.akuev.service;

import com.akuev.model.MovieSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для работы с сеансами фильмов.
 */
public interface MovieSessionService {

    /**
     * Получает список всех сеансов.
     *
     * @return список всех сеансов
     */
    List<MovieSession> findAll();

    /**
     * Находит сеанс по его идентификатору.
     *
     * @param id идентификатор сеанса
     * @return Optional с найденным сеансом или пустой, если сеанс не найден
     */
    Optional<MovieSession> findById(Long id);

    /**
     * Находит все сеансы для указанного фильма.
     *
     * @param id идентификатор фильма
     * @return список сеансов для указанного фильма
     */
    List<MovieSession> findMovieSessions(Long id);

    /**
     * Проверяет существование сеанса по идентификатору.
     *
     * @param id идентификатор сеанса
     * @return true, если сеанс существует, false - в противном случае
     */
    boolean existsById(Long id);

    /**
     * Создает новый сеанс.
     *
     * @param session сеанс для создания
     */
    void create(MovieSession session);

    /**
     * Создает новый сеанс для указанного фильма.
     *
     * @param movieId идентификатор фильма
     * @param session сеанс для создания
     * @return созданный сеанс
     */
    MovieSession createForMovie(Long movieId, MovieSession session);

    /**
     * Обновляет существующий сеанс.
     *
     * @param id идентификатор сеанса для обновления
     * @param session данные сеанса для обновления
     * @return обновленный сеанс
     */
    MovieSession update(Long id, MovieSession session);

    /**
     * Бронирует места на сеансе.
     *
     * @param sessionId идентификатор сеанса
     * @param seatsForBooking набор мест для бронирования
     * @return true, если бронирование успешно, false - если места уже заняты
     */
    boolean bookingSeats(Long sessionId, Set<String> seatsForBooking);

    /**
     * Освобождает забронированные места на сеансе.
     *
     * @param sessionId идентификатор сеанса
     * @param seatsForFree набор мест для освобождения
     */
    void freeBookingSeats(Long sessionId, Set<String> seatsForFree);

    /**
     * Удаляет сеанс по идентификатору.
     *
     * @param id идентификатор сеанса для удаления
     */
    void deleteById(Long id);
}