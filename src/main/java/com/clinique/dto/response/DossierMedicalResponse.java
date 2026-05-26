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
public class DossierMedicalResponse {
    private Long id;
    private PatientResponse patient;
    private MedecinResponse medecin;
    private LocalDateTime dateConsultation;
    private String notesMedecin;
    private String diagnostic;
    private String symptomes;
}
