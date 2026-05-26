package com.clinique.controller;

import com.clinique.dto.request.MedecinRequest;
import com.clinique.dto.response.ApiResponse;
import com.clinique.dto.response.DisponibilitePlageResponse;
import com.clinique.dto.response.MedecinResponse;
import com.clinique.enums.Specialite;
import com.clinique.service.DisponibiliteService;
import com.clinique.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@Tag(name = "Médecins", description = "Gestion des médecins")
@SecurityRequirement(name = "bearerAuth")
public class MedecinController {

    private final MedecinService medecinService;
    private final DisponibiliteService disponibiliteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un médecin (ADMIN)")
    public ResponseEntity<ApiResponse<MedecinResponse>> creerMedecin(@Valid @RequestBody MedecinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Médecin créé.", medecinService.creerMedecin(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Lister tous les médecins")
    public ResponseEntity<ApiResponse<List<MedecinResponse>>> listerMedecins() {
        return ResponseEntity.ok(ApiResponse.success("Liste des médecins.", medecinService.listerTousMedecins()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Obtenir un médecin par ID")
    public ResponseEntity<ApiResponse<MedecinResponse>> getMedecin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Médecin trouvé.", medecinService.getMedecinParId(id)));
    }

    @GetMapping("/specialite/{specialite}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Filtrer par spécialité")
    public ResponseEntity<ApiResponse<List<MedecinResponse>>> getParSpecialite(@PathVariable Specialite specialite) {
        return ResponseEntity.ok(ApiResponse.success("Médecins filtrés.", medecinService.getMedecinParSpecialite(specialite)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un médecin (ADMIN)")
    public ResponseEntity<ApiResponse<MedecinResponse>> mettreAJour(@PathVariable Long id, @Valid @RequestBody MedecinRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Médecin mis à jour.", medecinService.mettreAJourMedecin(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un médecin (ADMIN)")
    public ResponseEntity<ApiResponse<Void>> desactiver(@PathVariable Long id) {
        medecinService.desactiverMedecin(id);
        return ResponseEntity.ok(ApiResponse.success("Médecin désactivé.", null));
    }

    @GetMapping("/{id}/disponibilites")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Voir les disponibilités d'un médecin pour une date")
    public ResponseEntity<ApiResponse<List<DisponibilitePlageResponse>>> getDisponibilites(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Disponibilités.", disponibiliteService.getDisponibilites(id, date)));
    }
}
