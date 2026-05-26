package com.clinique.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class AuthRequest {

    @Data
    public static class LoginRequest {
        @Email(message = "Format d'email invalide")
        @NotBlank(message = "L'email est obligatoire")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        private String motDePasse;
    }

    @Data
    public static class InscriptionPatientRequest {
        @NotBlank(message = "Le nom est obligatoire")
        private String nom;

        @NotBlank(message = "Le prénom est obligatoire")
        private String prenom;

        @Email(message = "Format d'email invalide")
        @NotBlank(message = "L'email est obligatoire")
        private String email;

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        private String motDePasse;

        private String telephone;

        @NotNull(message = "La date de naissance est obligatoire")
        private LocalDate dateNaissance;
    }
}
