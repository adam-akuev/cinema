package com.akuev.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveSeatsRequest {
    private Set<String> seats;
}
