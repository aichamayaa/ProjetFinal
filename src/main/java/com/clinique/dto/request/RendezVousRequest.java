package com.clinique.dto.request;

import com.clinique.enums.RendezVousStatut;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RendezVousRequest {

    @NotNull(message = "La date et heure sont obligatoires")
    @Future(message = "La date du rendez-vous doit être dans le futur")
    private LocalDateTime dateHeure;

    private int dureeMinutes = 30;

    private String motif;

    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;

    // Utilisé uniquement par l'admin pour créer un RDV pour un patient spécifique
    private Long patientId;
}
