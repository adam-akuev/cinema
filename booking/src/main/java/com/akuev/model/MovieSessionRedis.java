package com.akuev.model;

import com.akuev.dto.MovieSessionResponseDTO;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("movie_session")
public class MovieSessionRedis {
    @Id
    private Long id;
    private LocalDateTime startTime;
    private Integer hallNumber;
    private double price;
    private Long movieId;
    private Set<String> availableSeats = new HashSet<>();
    private Set<String> bookedSeats = new HashSet<>();

    public MovieSessionRedis(MovieSessionResponseDTO dto) {
        this.id = dto.getId();
        this.startTime = dto.getStartTime();
        this.hallNumber = dto.getHallNumber();
        this.price = dto.getPrice();
        this.movieId = dto.getMovieId();
        this.availableSeats = new HashSet<>(dto.getAvailableSeats());
        this.bookedSeats = new HashSet<>(dto.getBookedSeats());
    }

    public boolean bookSeat(String seat) {
        if (availableSeats.remove(seat)) {
            bookedSeats.add(seat);
            return true;
        }
        return false;
    }

    public boolean releaseSeat(String seat) {
        if (bookedSeats.remove(seat)) {
            availableSeats.add(seat);
            return true;
        }
        return false;
    }

    public boolean isSeatAvailable(String seat) {
        return availableSeats.contains(seat);
    }
}
