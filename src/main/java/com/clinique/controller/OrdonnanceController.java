package com.clinique.controller;

import com.clinique.dto.request.OrdonnanceRequest;
import com.clinique.dto.response.ApiResponse;
import com.clinique.dto.response.OrdonnanceResponse;
import com.clinique.service.OrdonnanceService;
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
@RequestMapping("/api/ordonnances")
@RequiredArgsConstructor
@Tag(name = "Ordonnances", description = "Gestion des ordonnances")
@SecurityRequirement(name = "bearerAuth")
public class OrdonnanceController {

    private final OrdonnanceService ordonnanceService;

    @PostMapping
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Créer une ordonnance (MEDECIN traitant uniquement)")
    public ResponseEntity<ApiResponse<OrdonnanceResponse>> creer(
            @Valid @RequestBody OrdonnanceRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ordonnance créée.", ordonnanceService.creerOrdonnance(request, auth.getName())));
    }

    @GetMapping("/mes-ordonnances")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Voir mes ordonnances (PATIENT)")
    public ResponseEntity<ApiResponse<List<OrdonnanceResponse>>> mesOrdonnancesAlias(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Mes ordonnances.", ordonnanceService.getMesOrdonnances(auth.getName())));
    }

    @GetMapping("/moi")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Voir mes ordonnances (PATIENT)")
    public ResponseEntity<ApiResponse<List<OrdonnanceResponse>>> mesOrdonnances(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Mes ordonnances.", ordonnanceService.getMesOrdonnances(auth.getName())));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    @Operation(summary = "Toutes les ordonnances (ADMIN) / mes ordonnances médecin (MEDECIN)")
    public ResponseEntity<ApiResponse<List<OrdonnanceResponse>>> toutes(Authentication auth) {
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        if (isAdmin) {
            return ResponseEntity.ok(ApiResponse.success("Toutes les ordonnances.", ordonnanceService.getToutesOrdonnances()));
        } else {
            return ResponseEntity.ok(ApiResponse.success("Ordonnances médecin.", ordonnanceService.getOrdonnancesParMedecin(auth.getName())));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Détail d'une ordonnance")
    public ResponseEntity<ApiResponse<OrdonnanceResponse>> getOrdonnance(@PathVariable Long id, Authentication auth) {
        boolean isMedecin = hasRole(auth, "ROLE_MEDECIN");
        boolean isAdmin = hasRole(auth, "ROLE_ADMIN");
        return ResponseEntity.ok(ApiResponse.success("Ordonnance.", ordonnanceService.getOrdonnanceParId(id, auth.getName(), isMedecin, isAdmin)));
    }

    @GetMapping("/rendez-vous/{rdvId}")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Ordonnances d'un rendez-vous (MEDECIN traitant)")
    public ResponseEntity<ApiResponse<List<OrdonnanceResponse>>> getParRdv(@PathVariable Long rdvId, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Ordonnances.", ordonnanceService.getOrdonnancesParRendezVous(rdvId, auth.getName())));
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals(role));
    }
}
