package com.clinique.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilitePlageResponse {
    private LocalDateTime debut;
    private LocalDateTime fin;
    private int dureeMinutes;
    private boolean disponible;
}
