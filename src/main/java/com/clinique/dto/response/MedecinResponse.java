package com.clinique.dto.response;

import com.clinique.enums.Specialite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedecinResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Specialite specialite;
    private LocalTime heureDebutTravail;
    private LocalTime heureFinTravail;
    private int dureeConsultationMinutes;
    private boolean actif;
}
