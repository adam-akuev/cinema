package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Movie session response data")
public class MovieSessionResponseDTO {
    @Schema(
            description = "ID сеанса",
            example = "2"
    )
    private Long id;

    @Schema(
            description = "Время начала сеанса",
            example = "2025-09-08T13:30:00"
    )
    private LocalDateTime startTime;

    @Schema(
            description = "Номер зала",
            example = "2"
    )
    private int hallNumber;

    @Schema(
            description = "Цена сеанса",
            example = "600"
    )
    private double price;

    @Schema(
            description = "ID фильма",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private Long movieId;

    @Schema(
            description = "Свободные места",
            example = "A1 A2 A3 A4 A5 B3 B4 B5 C3"
    )
    private Set<String> availableSeats;

    @Schema(
            description = "Забронированные места",
            example = "A6 A7 A8 A9 A10 B1 B2 C1 C2"
    )
    private Set<String> bookedSeats;
}
