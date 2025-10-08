package com.akuev.dto;

import lombok.*;

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
}
