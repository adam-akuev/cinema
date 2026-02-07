package com.akuev.controller;

import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.dto.ReserveSeatsRequest;
import com.akuev.model.MovieSession;
import com.akuev.service.MovieSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movie-sessions/internal")
@RequiredArgsConstructor
@Tag(name = "Internal Movie Session Controller", description = "Внутренний API для операций с сеансами (бронирование мест)")
@SecurityRequirement(name = "bearerAuth")
public class MovieSessionResponseController {
    private final MovieSessionService sessionService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    @RolesAllowed({"ADMIN", "INTERNAL_SERVICE"})
    @Operation(summary = "Получить информацию о сеансе", description = "Внутренний метод для получения информации о сеансе по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о сеансе получена"),
            @ApiResponse(responseCode = "404", description = "Сеанс не найден")
    })
    public Optional<MovieSessionResponseDTO> findSessionById(@PathVariable("id") Long id) {
        return sessionService.findById(id).map(this::convertToDTO);
    }

    @PostMapping("/{sessionId}/booking-seats")
    @RolesAllowed({"ADMIN", "INTERNAL_SERVICE"})
    @Operation(summary = "Забронировать места", description = "Внутренний метод для бронирования мест на сеансе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Места успешно забронированы или недоступны"),
            @ApiResponse(responseCode = "404", description = "Сеанс не найден")
    })
    public boolean bookingSeatsForSession(@PathVariable("sessionId") Long sessionId,
                                               @RequestBody ReserveSeatsRequest request) {
        return sessionService.bookingSeats(sessionId, request.getSeats());
    }

    @PostMapping("/{sessionId}/free-seats")
    @RolesAllowed({"ADMIN", "INTERNAL_SERVICE"})
    @Operation(summary = "Освободить места", description = "Внутренний метод для освобождения забронированных мест")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Места успешно освобождены"),
            @ApiResponse(responseCode = "404", description = "Сеанс не найден")
    })
    public void freeSeatsForSession(@PathVariable("sessionId") Long sessionId,
                                          @RequestBody ReserveSeatsRequest request) {
        sessionService.freeBookingSeats(sessionId, request.getSeats());
    }

    private MovieSessionResponseDTO convertToDTO(MovieSession movieSession) {
        return modelMapper.map(movieSession, MovieSessionResponseDTO.class);
    }
}
