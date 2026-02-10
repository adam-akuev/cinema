package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Movie data")
public class MovieDTO {
    @Schema(
            description = "ID фильма",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private Long id;

    @Schema(
            description = "Название фильма",
            example = "Человек паук"
    )
    private String title;

    @Schema(
            description = "Описание фильма",
            example = "Человек-паук (Питер Паркер) — культовый супергерой Marvel, обычный школьник, получивший сверхспособности после укуса радиоактивного паука. Обладает силой, ловкостью, умением лазать по стенам и «паучьим чутьем»."
    )
    private String description;

    @Schema(
            description = "Жанр фильма",
            example = "Боевик"
    )
    private String genre;

    @Schema(
            description = "Длительность фильма",
            example = "148"
    )
    private int durationMinutes;
}
/*
Пример dto:
{
    "title": "Batman",
    "description": "Fantastic film Marvel",
    "genre": "Fantastic",
    "durationMinutes": 70
}
*/