package com.akuev.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private int durationMinutes;
}
