package com.akuev.controller;

import com.akuev.dto.BookingDTO;
import com.akuev.model.Booking;
import com.akuev.service.BookingService;
import com.akuev.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<BookingDTO> findAllBookings() {
        return bookingService.getAllBookings().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public Optional<BookingDTO> findById(@PathVariable("id") Long id) {
        return bookingService.getBookingById(id).map(this::convertToDTO);
    }

    @GetMapping("/user/{userId}")
    public List<BookingDTO> findBookingsByUserId(@PathVariable("userId") UUID userId) {
        return bookingService.findAllByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveBooking(@RequestBody BookingDTO bookingDTO) {
        Booking booking = convertToBooking(bookingDTO);

        log.debug("Save booking request. Correlation id: {}, User: {}, Session: {}",
                UserContextHolder.getContext().getCorrelationId(),
                booking.getUserId(),
                booking.getSessionId());

        bookingService.create(booking.getUserId(),
                booking.getSessionId(),
                booking.getBookedSeats());

        log.info("Booking created successfully. User: {}, Session: {}",
                booking.getUserId(),
                booking.getSessionId());
    }

    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBooking(@PathVariable("bookingId") Long bookingId) {
        log.debug("Remove booking request. Correlation id: {}, Booking id: {}",
                UserContextHolder.getContext().getCorrelationId(),
                bookingId);

        bookingService.cancelBooking(bookingId);

        log.info("Booking cancelled successfully. Booking id: {}", bookingId);
    }

    private BookingDTO convertToDTO(Booking booking) {
        return modelMapper.map(booking, BookingDTO.class);
    }

    private Booking convertToBooking(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setUserId(bookingDTO.getUserId());
        booking.setSessionId(bookingDTO.getSessionId());
        booking.setBookedSeats(bookingDTO.getBookedSeats());
        return booking;
    }
}
