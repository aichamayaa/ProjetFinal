package com.clinique.controller;

import com.clinique.dto.request.DossierMedicalRequest;
import com.clinique.dto.response.ApiResponse;
import com.clinique.dto.response.DossierMedicalResponse;
import com.clinique.service.DossierMedicalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dossiers-medicaux")
@RequiredArgsConstructor
@Tag(name = "Dossiers Médicaux", description = "Gestion des dossiers médicaux")
@SecurityRequirement(name = "bearerAuth")
public class DossierMedicalController {

    private final DossierMedicalService dossierMedicalService;

    @PostMapping
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Créer un dossier médical (MEDECIN)")
    public ResponseEntity<ApiResponse<DossierMedicalResponse>> creer(
            @Valid @RequestBody DossierMedicalRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dossier créé.", dossierMedicalService.creerDossier(request, auth.getName())));
    }

    @GetMapping("/mes-dossiers")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Voir mon historique médical (PATIENT)")
    public ResponseEntity<ApiResponse<List<DossierMedicalResponse>>> mesDossiers(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Mes dossiers.", dossierMedicalService.getMesDossiers(auth.getName())));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    @Operation(summary = "Dossiers d'un patient (ADMIN, MEDECIN)")
    public ResponseEntity<ApiResponse<List<DossierMedicalResponse>>> getParPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success("Dossiers.", dossierMedicalService.getDossiersParPatient(patientId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    @Operation(summary = "Tous les dossiers (ADMIN, MEDECIN)")
    public ResponseEntity<ApiResponse<List<DossierMedicalResponse>>> tousLesDossiers(Authentication auth) {
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        if (isAdmin) {
            return ResponseEntity.ok(ApiResponse.success("Tous les dossiers.", dossierMedicalService.getTousDossiers()));
        } else {

            return ResponseEntity.ok(ApiResponse.success("Mes dossiers médecin.", dossierMedicalService.getDossiersParMedecin(auth.getName())));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Détail d'un dossier")
    public ResponseEntity<ApiResponse<DossierMedicalResponse>> getDossier(@PathVariable Long id, Authentication auth) {
        boolean isMedecin = hasRole(auth, "ROLE_MEDECIN");
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        return ResponseEntity.ok(ApiResponse.success("Dossier.", dossierMedicalService.getDossierParId(id, auth.getName(), isMedecin, isAdmin)));
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals(role));
    }
}
