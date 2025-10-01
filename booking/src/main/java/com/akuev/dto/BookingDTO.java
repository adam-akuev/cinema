package com.akuev.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class BookingDTO {
    private Long sessionId;
    private Set<String> bookedSeats;

    /*{
            "sessionId": 2,
            "bookedSeats": ["B3", "B2", "B1"]
    }*/
}
