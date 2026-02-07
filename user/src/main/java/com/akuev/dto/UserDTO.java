package com.akuev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User data")
public class UserDTO {
    @Schema(
            description = "ID пользователя",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
            description = "Имя пользователя",
            example = "Иван"
    )
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов"
    )
    private String lastName;

    @Schema(
            description = "Email пользователя",
            example = "user@example.com"
    )
    private String email;
}