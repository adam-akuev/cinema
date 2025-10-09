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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<BookingDTO>> findAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("{id}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<BookingDTO> findById(@PathVariable("id") Long id) {
        Optional<BookingDTO> booking = bookingService.getBookingById(id).map(this::convertToDTO);
        return booking.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<BookingDTO>> findBookingsByUserId(@PathVariable("userId") UUID userId) {
        List<BookingDTO> bookings = bookingService.findAllByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-bookings")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<BookingDTO>> findMyBookings() {
        UUID currentUserId = getCurrentUserId();
        List<BookingDTO> bookings = bookingService.findAllByUserId(currentUserId).stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-bookings/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<BookingDTO> findMyBookingById(@PathVariable("id") Long id) {
        UUID currentUserId = getCurrentUserId();
        Optional<BookingDTO> booking = bookingService.getBookingByIdAndUserId(id, currentUserId).map(this::convertToDTO);
        return booking.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Void> saveBooking(@RequestBody BookingDTO bookingDTO) {
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

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{bookingId}")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<Void> cancelBooking(@PathVariable("bookingId") Long bookingId) {
        log.debug("Remove booking request. Correlation id: {}, Booking id: {}",
                UserContextHolder.getContext().getCorrelationId(),
                bookingId);

        bookingService.cancelBooking(bookingId);

        log.info("Booking cancelled successfully. Booking id: {}", bookingId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my-bookings/{bookingId}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Void> cancelMyBooking(@PathVariable("bookingId") Long bookingId) {
        UUID currentUserId = getCurrentUserId();

        log.debug("Cancel my booking request. User: {}, Booking id: {}",
                currentUserId, bookingId);

        bookingService.cancelBookingByUserId(bookingId, currentUserId);

        log.info("Booking cancelled by user. User: {}, Booking id: {}",
                currentUserId, bookingId);

        return ResponseEntity.noContent().build();
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