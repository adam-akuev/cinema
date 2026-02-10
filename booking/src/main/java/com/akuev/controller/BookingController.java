package com.akuev.controller;

import com.akuev.dto.BookingDTO;
import com.akuev.model.Booking;
import com.akuev.service.BookingService;
import com.akuev.util.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Booking Controller", description = "API для управления бронированиями билетов")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    @GetMapping
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Получить все бронирования", description = "Возвращает список всех бронирований (только для администратора)")
    @ApiResponse(responseCode = "200", description = "Успешно получен список бронирований")
    public ResponseEntity<List<BookingDTO>> findAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings().stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("{id}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Найти бронирование по ID", description = "Возвращает бронирование по указанному идентификатору (только для администратора)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронирование найдено"),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
    public ResponseEntity<BookingDTO> findById(@PathVariable("id") Long id) {
        Optional<BookingDTO> booking = bookingService.getBookingById(id).map(this::convertToDTO);
        return booking.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @RolesAllowed({"ADMIN"})
    @Operation(summary = "Найти бронирования пользователя", description = "Возвращает список бронирований указанного пользователя (только для администратора)")
    @ApiResponse(responseCode = "200", description = "Успешно получен список бронирований")
    public ResponseEntity<List<BookingDTO>> findBookingsByUserId(@PathVariable("userId") UUID userId) {
        List<BookingDTO> bookings = bookingService.findAllByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-bookings")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Получить свои бронирования", description = "Возвращает список бронирований текущего пользователя")
    @ApiResponse(responseCode = "200", description = "Успешно получен список бронирований")
    public ResponseEntity<List<BookingDTO>> findMyBookings() {
        UUID currentUserId = getCurrentUserId();
        List<BookingDTO> bookings = bookingService.findAllByUserId(currentUserId).stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/my-bookings/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Найти свое бронирование по ID", description = "Возвращает бронирование текущего пользователя по указанному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Бронирование найдено"),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
    public ResponseEntity<BookingDTO> findMyBookingById(@PathVariable("id") Long id) {
        UUID currentUserId = getCurrentUserId();
        Optional<BookingDTO> booking = bookingService.getBookingByIdAndUserId(id, currentUserId).map(this::convertToDTO);
        return booking.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Создать бронирование", description = "Создает новое бронирование билетов на сеанс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Бронирование успешно создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные или места недоступны")
    })
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
    @Operation(summary = "Отменить бронирование", description = "Отменяет бронирование по ID (только для администратора)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Бронирование успешно отменено"),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
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
    @Operation(summary = "Отменить свое бронирование", description = "Отменяет бронирование текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Бронирование успешно отменено"),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    })
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