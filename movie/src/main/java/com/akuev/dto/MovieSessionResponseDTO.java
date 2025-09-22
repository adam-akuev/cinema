package com.akuev.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class MovieSessionResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private int hallNumber;
    private double price;
    private Long movieId;
    private Set<String> availableSeats;
    private Set<String> bookedSeats;
}
