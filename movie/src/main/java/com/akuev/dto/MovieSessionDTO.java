package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Movie session data")
public class MovieSessionDTO {
    @Schema(
            description = "Время начала сеанса",
            example = "2025-09-08T13:30:00"
    )
    private LocalDateTime startTime;

    @Schema(
            description = "Номер зала",
            example = "3"
    )
    private int hallNumber;

    @Schema(
            description = "Цена сеанса",
            example = "600"
    )
    private double price;

    @Schema(
            description = "ID фильма",
            example = "2"
    )
    private Long movieId;
}
/*
Пример dto:
{
    "startTime": "2025-09-08T13:30:00",
        "hallNumber": 3,
        "price": 600,
        "movieId": 2
}
*/