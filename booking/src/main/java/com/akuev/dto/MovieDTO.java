package com.akuev.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private int durationMinutes;
}
