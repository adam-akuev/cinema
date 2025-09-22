package com.akuev.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDTO {
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
