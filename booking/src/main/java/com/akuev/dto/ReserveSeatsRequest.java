package com.akuev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ReserveSeatsRequest {
    private Set<String> seats;
}
