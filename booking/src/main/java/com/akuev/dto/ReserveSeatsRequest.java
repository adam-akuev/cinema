package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос для бронирования или освобождения мест")
public class ReserveSeatsRequest {

    @Schema(description = "Набор мест",
            example = "[\"A1\", \"A2\", \"A3\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> seats;
}
