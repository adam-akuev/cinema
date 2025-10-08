package com.akuev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    /*{
        "title": "Batman",
            "description": "Fantastic film Marvel",
            "genre": "Fantastic",
            "durationMinutes": 70
    }*/
