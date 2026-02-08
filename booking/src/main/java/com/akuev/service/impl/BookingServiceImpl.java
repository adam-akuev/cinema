package com.akuev.service.impl;

import com.akuev.exception.*;
import com.akuev.model.MovieSessionRedis;
import com.akuev.repository.MovieSessionRedisRepository;
import com.akuev.service.BookingCacheService;
import com.akuev.service.BookingService;
import com.akuev.service.client.MovieFeignClient;
import com.akuev.service.client.UserFeignClient;
import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.dto.ReserveSeatsRequest;
import com.akuev.dto.UserDTO;
import com.akuev.model.Booking;
import com.akuev.repository.BookingRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Реализация сервиса для управления бронированиями.
 * Использует паттерны устойчивости (circuit breaker, bulkhead, retry) для обработки сбоев.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final MovieSessionRedisRepository movieSessionRedisRepository;
    private final BookingCacheService bookingCacheService;
    private final BookingRepository bookingRepository;
    private final MovieFeignClient movieFeignClient;
    private final UserFeignClient userFeignClient;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void create(UUID userId, Long sessionId, Set<String> seatsForBooking) {
        log.info("Creating booking for user: {}, session: {}, seats: {}", userId, sessionId, seatsForBooking);

        Optional<MovieSessionRedis> cachedSession = bookingCacheService.getCachedSession(sessionId);
        MovieSessionResponseDTO movieSession;
        UserDTO user;

        if (cachedSession.isPresent()) {
            // Проверка доступности мест в кэше
            for (String seat : seatsForBooking) {
                if (!cachedSession.get().isSeatAvailable(seat)) {
                    throw new SeatAlreadyBookedException("Seats already booked in cache: " + seatsForBooking);
                }
            }

            user = getUser(userId);
            movieSession = new MovieSessionResponseDTO(cachedSession.get());

            log.debug("Seats available in cache: {}", seatsForBooking);
        } else {
            log.debug("Session in not cache, using Feign");
            if (!checkSeatsAvailability(sessionId, seatsForBooking)) {
                throw new SeatAlreadyBookedException("Some seats are already booked: " + seatsForBooking);
            }

            user = getUser(userId);
            movieSession = getSession(sessionId);
        }

        ReserveSeatsRequest request = new ReserveSeatsRequest(seatsForBooking);
        boolean bookingSuccess = getBookingSeats(sessionId, request);

        if (!bookingSuccess) {
            throw new BookingFailedException("Failed to book seats in movie service");
        }

        if (cachedSession.isPresent()) {
            // Обновление кэша после успешного бронирования
            MovieSessionRedis session = cachedSession.get();
            for (String seat : seatsForBooking) {
                session.bookSeat(seat);
            }
            movieSessionRedisRepository.save(session);
            log.debug("Updated cache after booking: {}", seatsForBooking);
        } else {
            bookingCacheService.syncSessionToCache(sessionId);
        }

        // Создание и сохранение бронирования
        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setSessionId(movieSession.getId());
        booking.setBookedSeats(seatsForBooking);
        booking.setPaid(true);

        saveBooking(booking);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "getAllBookingsFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getAllBookingsFallback")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "getBookingByIdFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getBookingByIdFallback")
    public Optional<Booking> getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking with id " + id + " not found!");
        }
        return booking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "getBookingByIdFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getBookingByIdFallback")
    public Optional<Booking> getBookingByIdAndUserId(Long id, UUID userId) {
        Optional<Booking> booking = bookingRepository.findByIdAndUserId(id, userId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking with id " + id + " not found!");
        }
        return booking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "findAllByUserIdFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "findAllByUserIdFallback")
    public List<Booking> findAllByUserId(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    /**
     * Проверяет доступность мест на сеансе.
     */
    private boolean checkSeatsAvailability(Long sessionId, Set<String> seats) {
        MovieSessionResponseDTO session = getSession(sessionId);
        Set<String> availableSeats = new HashSet<>(session.getAvailableSeats());
        return availableSeats.containsAll(seats);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "getBookingSeatsFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getBookingSeatsFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "getBookingSeatsFallback")
    public boolean getBookingSeats(Long sessionId, ReserveSeatsRequest request) {
        return movieFeignClient.bookingSeatsForSession(sessionId, request);
    }

    /**
     * Получает информацию о пользователе через Feign клиент.
     */
    @CircuitBreaker(name = "userServiceClient", fallbackMethod = "getUserFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getUserFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "getUserFallback")
    private UserDTO getUser(UUID userId) {
        return userFeignClient.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id " + userId + " not found!"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "getSessionFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getSessionFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "getSessionFallback")
    public MovieSessionResponseDTO getSession(Long sessionId) {
        return movieFeignClient.findSessionById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session by id " + sessionId + " not found!"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "saveBookingFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "saveBookingFallback")
    public void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking by id " + bookingId + " not found!"));

        ReserveSeatsRequest freeRequest = new ReserveSeatsRequest(booking.getBookedSeats());
        try {
            freeBookingSeats(booking.getSessionId(), freeRequest);
            deleteBookingById(bookingId);
        } catch (ServiceAccessDeniedException e) {
            throw new BookingFailedException("Failed to free seats in movie service: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancelBookingByUserId(Long bookingId, UUID userId) {
        Booking booking = getBookingById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking by id " + bookingId + " not found!"));

        if (!bookingRepository.existsByIdAndUserId(bookingId, userId)) {
            throw new SecurityException("Booking not found or access denied");
        }

        ReserveSeatsRequest freeRequest = new ReserveSeatsRequest(booking.getBookedSeats());
        try {
            freeBookingSeats(booking.getSessionId(), freeRequest);
            deleteBookingById(bookingId);
        } catch (ServiceAccessDeniedException e) {
            throw new BookingFailedException("Failed to free seats in movie service: " + e.getMessage());
        }
    }

    /**
     * Освобождает места на сеансе через Feign клиент.
     */
    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "freeBookingSeatsFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "freeBookingSeatsFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "freeBookingSeatsFallback")
    private void freeBookingSeats(Long sessionId, ReserveSeatsRequest freeRequest) {
        movieFeignClient.freeSeatsForSession(sessionId, freeRequest);
    }

    /**
     * Удаляет бронирование по идентификатору.
     */
    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "deleteBookingFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "deleteBookingFallback")
    private void deleteBookingById(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // Fallback методы для обработки сбоев

    /**
     * Fallback метод для получения всех бронирований.
     */
    private List<Booking> getAllBookingsFallback(Exception e) {
        return Collections.emptyList();
    }

    /**
     * Fallback метод для получения бронирования по ID.
     */
    private Optional<Booking> getBookingByIdFallback(Long id, Exception e) {
        return Optional.empty();
    }

    /**
     * Fallback метод для получения бронирований пользователя.
     */
    private List<Booking> findAllByUserIdFallback(UUID userId, Exception e) {
        return Collections.emptyList();
    }

    /**
     * Fallback метод для проверки доступности мест.
     */
    private boolean checkSeatsAvailabilityFallback(Long sessionId, Set<String> seats, Exception e) {
        throw new BookingFailedException("Cannot check seats availability: Service unavailable");
    }

    /**
     * Fallback метод для бронирования мест.
     */
    private boolean getBookingSeatsFallback(Long sessionId, Set<String> seats, Exception e) {
        throw new BookingFailedException("Cannot reserve seats: Service unavailable");
    }

    /**
     * Fallback метод для получения информации о пользователе.
     */
    private UserDTO getUserFallback(UUID userId, Exception e) {
        throw new BookingFailedException("Cannot get user: Service unavailable");
    }

    /**
     * Fallback метод для получения информации о сеансе.
     */
    private MovieSessionResponseDTO getSessionFallback(Long sessionId, Exception e) {
        throw new BookingFailedException("Cannot get session: Service unavailable");
    }

    /**
     * Fallback метод для сохранения бронирования.
     */
    private void saveBookingFallback(UUID userId, Long sessionId, Set<String> seats, Exception e) {
        throw new BookingFailedException("Cannot save booking: Database unavailable");
    }

    /**
     * Fallback метод для освобождения мест.
     */
    private void freeBookingSeatsFallback(Long sessionId, Set<String> seats, Exception e) {
        // TODO I need to free seats!
    }

    /**
     * Fallback метод для удаления бронирования.
     */
    private void deleteBookingFallback(Long bookingId, Exception e) {
        throw new BookingFailedException("Cannot delete booking: Database unavailable");
    }
}