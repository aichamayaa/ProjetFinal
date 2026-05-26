package com.clinique.dto.response;

import com.clinique.enums.RendezVousStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousResponse {
    private Long id;
    private LocalDateTime dateHeure;
    private int dureeMinutes;
    private RendezVousStatut statut;
    private String motif;
    private MedecinResponse medecin;
    private PatientResponse patient;
}
