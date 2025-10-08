package com.akuev.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long sessionId;
    private Set<String> bookedSeats;

    /*{
            "sessionId": 2,
            "bookedSeats": ["B3", "B2", "B1"]
    }*/
}
