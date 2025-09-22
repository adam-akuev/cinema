package com.akuev.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
