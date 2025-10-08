package com.akuev.dto;

import com.akuev.model.MovieSessionRedis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSessionResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private int hallNumber;
    private double price;
    private Long movieId;
    private Set<String> availableSeats;
    private Set<String> bookedSeats;

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
