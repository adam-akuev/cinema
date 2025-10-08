package com.akuev.event.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSessionChangeModel {
    private String type;
    private String action;
    private Long sessionId;
    private String correlationId;
}
