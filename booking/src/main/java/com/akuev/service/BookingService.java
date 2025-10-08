package com.akuev.service;

import com.akuev.exception.*;
import com.akuev.model.MovieSessionRedis;
import com.akuev.repository.MovieSessionRedisRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {
    private final MovieSessionRedisRepository movieSessionRedisRepository;
    private final BookingRepository bookingRepository;
    private final MovieFeignClient movieFeignClient;
    private final UserFeignClient userFeignClient;

    // ===== REDIS =====
    public void cacheMovieSession(MovieSessionResponseDTO sessionDTO) {
        try {
            MovieSessionRedis sessionRedis = new MovieSessionRedis(sessionDTO);
            movieSessionRedisRepository.save(sessionRedis);
            log.debug("Cached session in Redis: {}", sessionDTO.getId());
        } catch (Exception e) {
            log.error("Failed to cache session: {}", sessionDTO.getId(), e);
        }
    }

    public Optional<MovieSessionRedis> getCachedSession(Long sessionId) {
        try {
            return movieSessionRedisRepository.findById(sessionId);
        } catch (Exception e) {
            log.error("Failed to get cached session: {}", sessionId);
            return Optional.empty();
        }
    }

    public void evictCachedSession(Long sessionId) {
        try {
            movieSessionRedisRepository.deleteById(sessionId);
            log.debug("Evicted session from Redis: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to evict session: {}", sessionId, e);
        }
    }

    public void syncSessionToCache(Long sessionId) {
        try {
            MovieSessionResponseDTO sessionDTO = getSession(sessionId);
            cacheMovieSession(sessionDTO);
            log.debug("Synced session to Redis cache: {}", sessionId);
        } catch (Exception e) {
            log.error("Failed to sync session to cache: {}", sessionId, e);
        }
    }

    @Transactional
    public void create(UUID userId, Long sessionId, Set<String> seatsForBooking) {
        log.info("Creating booking for user: {}, session: {}, seats: {}", userId, sessionId, seatsForBooking);

        Optional<MovieSessionRedis> cachedSession = getCachedSession(sessionId);
        MovieSessionResponseDTO movieSession;
        UserDTO user;

        if (cachedSession.isPresent()) {
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
            MovieSessionRedis session = cachedSession.get();
            for (String seat : seatsForBooking) {
                session.bookSeat(seat);
            }
            movieSessionRedisRepository.save(session);
            log.debug("Updated cache after booking: {}", seatsForBooking);
        } else {
            syncSessionToCache(sessionId);
        }

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setSessionId(movieSession.getId());
        booking.setBookedSeats(seatsForBooking);
        booking.setPaid(true);

        saveBooking(booking);
    }

    // ===== END =====

    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "getAllBookingsFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getAllBookingsFallback")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "getBookingByIdFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getBookingByIdFallback")
    public Optional<Booking> getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking with id " + id + " not found!");
        }
        return booking;
    }

    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "getBookingByIdFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getBookingByIdFallback")
    public Optional<Booking> getBookingByIdAndUserId(Long id, UUID userId) {
        Optional<Booking> booking = bookingRepository.findByIdAndUserId(id, userId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking with id " + id + " not found!");
        }
        return booking;
    }

    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "findAllByUserIdFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "findAllByUserIdFallback")
    public List<Booking> findAllByUserId(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    private boolean checkSeatsAvailability(Long sessionId, Set<String> seats) {
        MovieSessionResponseDTO session = getSession(sessionId);
        Set<String> availableSeats = new HashSet<>(session.getAvailableSeats());
        return availableSeats.containsAll(seats);
    }

//    @Transactional
//    public void create(UUID userId, Long sessionId, Set<String> seatsForBooking) {
//        if (!checkSeatsAvailability(sessionId, seatsForBooking)) {
//            throw new SeatAlreadyBookedException("Some seats are already booked: " + seatsForBooking);
//        }
//
//        ReserveSeatsRequest request = new ReserveSeatsRequest(seatsForBooking);
//        boolean bookingSuccess = getBookingSeats(sessionId, request);
//
//        if (!bookingSuccess) {
//            throw new BookingFailedException("Failed to book seats in movie service");
//        }
//
//        UserDTO user = getUser(userId);
//        MovieSessionResponseDTO movieSession = getSession(sessionId);
//
//        Booking booking = new Booking();
//        booking.setUserId(user.getId());
//        booking.setSessionId(movieSession.getId());
//        booking.setBookedSeats(seatsForBooking);
//        booking.setPaid(true);
//
//        saveBooking(booking);
//    }

    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "getBookingSeatsFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getBookingSeatsFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "getBookingSeatsFallback")
    private boolean getBookingSeats(Long sessionId, ReserveSeatsRequest request) {
        return movieFeignClient.bookingSeatsForSession(sessionId, request);
    }

    @CircuitBreaker(name = "userServiceClient", fallbackMethod = "getUserFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getUserFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "getUserFallback")
    private UserDTO getUser(UUID userId) {
        return userFeignClient.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User by id " + userId + " not found!"));
    }

    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "getSessionFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "getSessionFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "getSessionFallback")
    private MovieSessionResponseDTO getSession(Long sessionId) {
        return movieFeignClient.findSessionById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session by id " + sessionId + " not found!"));
    }

    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "saveBookingFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "saveBookingFallback")
    private void saveBooking(Booking booking) {
        bookingRepository.save(booking);
    }

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

    @CircuitBreaker(name = "movieServiceClient", fallbackMethod = "freeBookingSeatsFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "freeBookingSeatsFallback")
    @Retry(name = "BookingServiceRetry", fallbackMethod = "freeBookingSeatsFallback")
    private void freeBookingSeats(Long sessionId, ReserveSeatsRequest freeRequest) {
        movieFeignClient.freeSeatsForSession(sessionId, freeRequest);
    }

    @CircuitBreaker(name = "bookingDatabase", fallbackMethod = "deleteBookingFallback")
    @Bulkhead(name = "bulkheadBookingService", fallbackMethod = "deleteBookingFallback")
    private void deleteBookingById(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // Fallback
    private List<Booking> getAllBookingsFallback(Exception e) {
        return Collections.emptyList();
    }

    private Optional<Booking> getBookingByIdFallback(Long id, Exception e) {
        return Optional.empty();
    }

    private List<Booking> findAllByUserIdFallback(UUID userId, Exception e) {
        return Collections.emptyList();
    }

    private boolean checkSeatsAvailabilityFallback(Long sessionId, Set<String> seats, Exception e) {
        throw new BookingFailedException("Cannot check seats availability: Service unavailable");
    }

    private boolean getBookingSeatsFallback(Long sessionId, Set<String> seats, Exception e) {
        throw new BookingFailedException("Cannot reserve seats: Service unavailable");
    }

    private UserDTO getUserFallback(UUID userId, Exception e) {
        throw new BookingFailedException("Cannot get user: Service unavailable");
    }

    private MovieSessionResponseDTO getSessionFallback(Long sessionId, Exception e) {
        throw new BookingFailedException("Cannot get session: Service unavailable");
    }

    private void saveBookingFallback(UUID userId, Long sessionId, Set<String> seats, Exception e) {
        throw new BookingFailedException("Cannot save booking: Database unavailable");
    }

    private void freeBookingSeatsFallback(Long sessionId, Set<String> seats, Exception e) {
        // TODO I need to free seats!
    }

    private void deleteBookingFallback(Long bookingId, Exception e) {
        throw new BookingFailedException("Cannot delete booking: Database unavailable");
    }
}
