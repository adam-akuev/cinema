package com.akuev.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class BookingDTO {
    private UUID userId;
    private Long sessionId;
    private Set<String> bookedSeats;

    /*{
        "userId": 1,
            "sessionId": 2,
            "bookedSeats": ["B3", "B2", "B1"]
    }*/
}
