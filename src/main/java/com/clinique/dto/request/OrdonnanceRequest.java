package com.clinique.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrdonnanceRequest {

    @NotNull(message = "L'ID du rendez-vous est obligatoire")
    private Long rendezVousId;

    @NotBlank(message = "Les médicaments sont obligatoires")
    private String medicaments;

    @NotBlank(message = "La posologie est obligatoire")
    private String posologie;

    @NotNull(message = "La date d'expiration est obligatoire")
    @Future(message = "La date d'expiration doit être dans le futur")
    private LocalDate dateValiditeExpiration;

    private String instructionsSpeciales;
}
