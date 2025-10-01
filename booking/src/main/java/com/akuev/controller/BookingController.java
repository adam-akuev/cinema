package com.akuev.controller;

import com.akuev.dto.BookingDTO;
import com.akuev.model.Booking;
import com.akuev.service.BookingService;
import com.akuev.util.UserContextHolder;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RolesAllowed({"ADMIN"})
    public List<BookingDTO> findAllBookings() {
        return bookingService.getAllBookings().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @RolesAllowed({"ADMIN"})
    public Optional<BookingDTO> findById(@PathVariable("id") Long id) {
        return bookingService.getBookingById(id).map(this::convertToDTO);
    }

    @GetMapping("/user/{userId}")
    @RolesAllowed({"ADMIN"})
    public List<BookingDTO> findBookingsByUserId(@PathVariable("userId") UUID userId) {
        return bookingService.findAllByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/my-bookings")
    @RolesAllowed({"USER", "ADMIN"})
    public List<BookingDTO> findMyBookings() {
        UUID currentUserId = getCurrentUserId();
        return bookingService.findAllByUserId(currentUserId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/my-bookings/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Optional<BookingDTO> findMyBookingById(@PathVariable("id") Long id) {
        UUID currentUserId = getCurrentUserId();
        return bookingService.getBookingByIdAndUserId(id, currentUserId).map(this::convertToDTO);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed({"USER", "ADMIN"})
    public void saveBooking(@RequestBody BookingDTO bookingDTO) {
        UUID userId = getCurrentUserId();

        log.debug("Save booking request. Correlation id: {}, User: {}, Session: {}",
                UserContextHolder.getContext().getCorrelationId(),
                userId,
                bookingDTO.getSessionId());

        bookingService.create(userId,
                bookingDTO.getSessionId(),
                bookingDTO.getBookedSeats());

        log.info("Booking created successfully. User: {}, Session: {}",
                userId,
                bookingDTO.getSessionId());
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed({"ADMIN"})
    public void cancelBooking(@PathVariable("bookingId") Long bookingId) {
        log.debug("Remove booking request. Correlation id: {}, Booking id: {}",
                UserContextHolder.getContext().getCorrelationId(),
                bookingId);

        bookingService.cancelBooking(bookingId);

        log.info("Booking cancelled successfully. Booking id: {}", bookingId);
    }

    @DeleteMapping("/my-bookings/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed({"USER", "ADMIN"})
    public void cancelMyBooking(@PathVariable("bookingId") Long bookingId) {
        UUID currentUserId = getCurrentUserId();

        log.debug("Cancel my booking request. User: {}, Booking id: {}",
                currentUserId, bookingId);

        bookingService.cancelBookingByUserId(bookingId, currentUserId);

        log.info("Booking cancelled by user. User: {}, Booking id: {}",
                currentUserId, bookingId);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String userId = jwt.getSubject();
            return UUID.fromString(userId);
        }
        throw new SecurityException("User not authenticated!");
    }

    private BookingDTO convertToDTO(Booking booking) {
        return modelMapper.map(booking, BookingDTO.class);
    }
}
