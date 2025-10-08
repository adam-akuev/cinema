package com.akuev.controller;

import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.dto.ReserveSeatsRequest;
import com.akuev.model.MovieSession;
import com.akuev.service.MovieSessionService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movie-sessions/internal")
@RequiredArgsConstructor
public class MovieSessionResponseController {
    private final MovieSessionService sessionService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "INTERNAL_SERVICE"})
    public Optional<MovieSessionResponseDTO> findSessionById(@PathVariable("id") Long id) {
        return sessionService.findById(id).map(this::convertToDTO);
    }

    @PostMapping("/{sessionId}/booking-seats")
    @RolesAllowed({"ADMIN", "INTERNAL_SERVICE"})
    public boolean bookingSeatsForSession(@PathVariable("sessionId") Long sessionId,
                                               @RequestBody ReserveSeatsRequest request) {
        return sessionService.bookingSeats(sessionId, request.getSeats());
    }

    @PostMapping("/{sessionId}/free-seats")
    @RolesAllowed({"ADMIN", "INTERNAL_SERVICE"})
    public void freeSeatsForSession(@PathVariable("sessionId") Long sessionId,
                                          @RequestBody ReserveSeatsRequest request) {
        sessionService.freeBookingSeats(sessionId, request.getSeats());
    }

    private MovieSessionResponseDTO convertToDTO(MovieSession movieSession) {
        return modelMapper.map(movieSession, MovieSessionResponseDTO.class);
    }
}
