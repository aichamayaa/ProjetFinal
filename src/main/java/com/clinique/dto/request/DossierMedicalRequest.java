package com.clinique.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DossierMedicalRequest {

    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;

    private String diagnostic;
    private String symptomes;
    private String notesMedecin;
}
