package com.akuev.service.client;

import com.akuev.dto.MovieSessionResponseDTO;
import com.akuev.dto.ReserveSeatsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient("movieservice")
public interface MovieFeignClient {
    @GetMapping("/api/movie-sessions/internal/{id}")
    Optional<MovieSessionResponseDTO> findSessionById(@PathVariable("id") Long id);

    @PostMapping("/api/movie-sessions/internal/{sessionId}/booking-seats")
    boolean bookingSeatsForSession(@PathVariable("sessionId") Long sessionId, @RequestBody ReserveSeatsRequest requestSeats);

    @PostMapping("/api/movie-sessions/internal/{sessionId}/free-seats")
    void freeSeatsForSession(@PathVariable("sessionId") Long sessionId, @RequestBody ReserveSeatsRequest request);
}
