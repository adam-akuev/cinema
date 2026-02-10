package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о фильме")
public class MovieDTO {

    @Schema(description = "ID фильма", example = "1")
    private Long id;

    @Schema(description = "Название фильма", example = "Матрица", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Описание фильма", example = "Фильм о виртуальной реальности")
    private String description;

    @Schema(description = "Жанр фильма", example = "фантастика")
    private String genre;

    @Schema(description = "Длительность фильма в минутах", example = "136", minimum = "1")
    private int durationMinutes;
}
