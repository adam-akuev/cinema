package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные для создания бронирования")
public class BookingDTO {

    @Schema(description = "ID сеанса фильма", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sessionId;

    @Schema(description = "Набор мест для бронирования",
            example = "[\"B3\", \"B2\", \"B1\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> bookedSeats;
}
/*
Пример для создания dto:
{
    "sessionId": 2,
    "bookedSeats": ["B3", "B2", "B1"]
}
*/