package com.clinique.dto.request;

import com.clinique.enums.Specialite;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class MedecinRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @NotNull(message = "La spécialité est obligatoire")
    private Specialite specialite;

    @NotNull(message = "L'heure de début de travail est obligatoire")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureDebutTravail;

    @NotNull(message = "L'heure de fin de travail est obligatoire")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureFinTravail;

    private int dureeConsultationMinutes = 30;
}
