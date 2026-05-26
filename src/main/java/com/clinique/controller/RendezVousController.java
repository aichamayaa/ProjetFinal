package com.clinique.controller;

import com.clinique.dto.request.RendezVousRequest;
import com.clinique.dto.response.ApiResponse;
import com.clinique.dto.response.RendezVousResponse;
import com.clinique.enums.Role;
import com.clinique.service.RendezVousService;
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
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
@Tag(name = "Rendez-vous", description = "Gestion des rendez-vous")
@SecurityRequirement(name = "bearerAuth")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','MEDECIN')")
    @Operation(summary = "Prendre un rendez-vous (PATIENT, ADMIN, MEDECIN)")
    public ResponseEntity<ApiResponse<RendezVousResponse>> prendreRdv(
            @Valid @RequestBody RendezVousRequest request, Authentication auth) {
        Role role = getRole(auth);
        if (role == Role.ROLE_ADMIN || role == Role.ROLE_MEDECIN) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Rendez-vous créé.", rendezVousService.prendreRendezVousAdmin(request)));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rendez-vous créé.", rendezVousService.prendreRendezVous(request, auth.getName())));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un rendez-vous pour n'importe quel patient (ADMIN)")
    public ResponseEntity<ApiResponse<RendezVousResponse>> prendreRdvAdmin(
            @Valid @RequestBody RendezVousRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rendez-vous créé.", rendezVousService.prendreRendezVousAdmin(request)));
    }

    @GetMapping("/mes-rendez-vous")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Lister mes rendez-vous (selon le rôle)")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> mesRdvDedicated(Authentication auth) {
        Role role = getRole(auth);
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous.", rendezVousService.getMesRendezVous(auth.getName(), role)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Lister mes rendez-vous (selon le rôle)")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> mesRdv(Authentication auth) {
        Role role = getRole(auth);
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous.", rendezVousService.getMesRendezVous(auth.getName(), role)));
    }

    @GetMapping("/tous")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les rendez-vous (ADMIN)")
    public ResponseEntity<ApiResponse<List<RendezVousResponse>>> tousRdv() {
        return ResponseEntity.ok(ApiResponse.success("Tous les rendez-vous.", rendezVousService.getTousRendezVous()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Détail d'un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> getRdv(@PathVariable Long id, Authentication auth) {
        Role role = getRole(auth);
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous.", rendezVousService.getRendezVousParId(id, auth.getName(), role)));
    }

    @PatchMapping("/{id}/confirmer")
    @PreAuthorize("hasAnyRole('MEDECIN','ADMIN')")
    @Operation(summary = "Confirmer un rendez-vous (MEDECIN, ADMIN)")
    public ResponseEntity<ApiResponse<RendezVousResponse>> confirmer(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous confirmé.", rendezVousService.confirmerRendezVous(id, auth.getName())));
    }

    @PutMapping("/{id}/confirmer")
    @PreAuthorize("hasAnyRole('MEDECIN','ADMIN')")
    @Operation(summary = "Confirmer un rendez-vous (MEDECIN, ADMIN) - PUT alias")
    public ResponseEntity<ApiResponse<RendezVousResponse>> confirmerPut(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous confirmé.", rendezVousService.confirmerRendezVous(id, auth.getName())));
    }

    @PatchMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Annuler un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> annuler(@PathVariable Long id, Authentication auth) {
        Role role = getRole(auth);
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous annulé.", rendezVousService.annulerRendezVous(id, auth.getName(), role)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','PATIENT')")
    @Operation(summary = "Annuler/supprimer un rendez-vous")
    public ResponseEntity<ApiResponse<RendezVousResponse>> annulerDelete(@PathVariable Long id, Authentication auth) {
        Role role = getRole(auth);
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous annulé.", rendezVousService.annulerRendezVous(id, auth.getName(), role)));
    }

    @PatchMapping("/{id}/terminer")
    @PreAuthorize("hasAnyRole('MEDECIN','ADMIN')")
    @Operation(summary = "Marquer un rendez-vous comme terminé (MEDECIN)")
    public ResponseEntity<ApiResponse<RendezVousResponse>> terminer(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous terminé.", rendezVousService.terminerRendezVous(id, auth.getName())));
    }

    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasAnyRole('MEDECIN','ADMIN')")
    @Operation(summary = "Marquer un rendez-vous comme terminé (MEDECIN) - PUT alias")
    public ResponseEntity<ApiResponse<RendezVousResponse>> terminerPut(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Rendez-vous terminé.", rendezVousService.terminerRendezVous(id, auth.getName())));
    }

    private Role getRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .findFirst()
                .orElseThrow();
    }
}
