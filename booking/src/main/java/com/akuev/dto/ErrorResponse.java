package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа об ошибке.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация об ошибке")
public class ErrorResponse {
    
    @Schema(description = "Сообщение об ошибке", example = "Фильм с указанным ID не найден")
    private String message;
    
    @Schema(description = "Временная метка ошибки", example = "1645432100000")
    private long timestamp;
}