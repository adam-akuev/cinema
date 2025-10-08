package com.akuev.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSessionDTO {
    private LocalDateTime startTime;
    private int hallNumber;
    private double price;
    private Long movieId;
}
    /*{
        "startTime": "2025-09-08T13:30:00",
            "hallNumber": 3333,
            "price": 6000000,
            "movieId": 2
    }*/
