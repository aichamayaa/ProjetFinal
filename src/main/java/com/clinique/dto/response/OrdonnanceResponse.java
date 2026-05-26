package com.clinique.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceResponse {
    private Long id;
    private Long rendezVousId;
    private LocalDateTime dateCreation;
    private String medicaments;
    private String posologie;
    private LocalDate dateValiditeExpiration;
    private String instructionsSpeciales;
    private MedecinResponse medecin;
    private PatientResponse patient;
}
