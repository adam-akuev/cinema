package com.akuev.dto;

import com.akuev.model.MovieSessionRedis;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о сеансе фильма")
public class MovieSessionResponseDTO {

    @Schema(description = "ID сеанса", example = "1")
    private Long id;

    @Schema(description = "Время начала сеанса", example = "2024-01-15T18:30:00")
    private LocalDateTime startTime;

    @Schema(description = "Номер зала", example = "3", minimum = "1")
    private int hallNumber;

    @Schema(description = "Цена билета", example = "350.0", minimum = "0")
    private double price;

    @Schema(description = "ID фильма", example = "2")
    private Long movieId;

    @Schema(description = "Доступные места", example = "[\"B1\", \"B2\"]")
    private Set<String> availableSeats;

    @Schema(description = "Забронированные места", example = "[\"A1\", \"A2\", \"A3\"]")
    private Set<String> bookedSeats;

    /**
     * Конструктор для создания DTO из Redis-сущности.
     *
     * @param redis сущность сеанса из Redis
     */
    public MovieSessionResponseDTO(MovieSessionRedis redis) {
        this.id = redis.getId();
        this.startTime = redis.getStartTime();
        this.hallNumber = redis.getHallNumber();
        this.price = redis.getPrice();
        this.movieId = redis.getMovieId();
        this.availableSeats = redis.getAvailableSeats();
        this.bookedSeats = redis.getBookedSeats();
    }
}
