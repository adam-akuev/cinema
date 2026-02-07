package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reserve seats data")
public class ReserveSeatsRequest {
    @Schema(
            description = "Места, которые хотят забронировать",
            example = "A6 A7"
    )
    private Set<String> seats;
}