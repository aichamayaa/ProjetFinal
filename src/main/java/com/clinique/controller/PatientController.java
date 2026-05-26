package com.clinique.controller;

import com.clinique.dto.response.ApiResponse;
import com.clinique.dto.response.PatientResponse;
import com.clinique.service.AuthService;
import com.clinique.service.PatientService;
import com.clinique.dto.request.AuthRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Gestion des patients")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;
    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    @Operation(summary = "Lister tous les patients (ADMIN, MEDECIN)")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> listerPatients() {
        return ResponseEntity.ok(ApiResponse.success("Liste des patients.", patientService.listerTousPatients()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    @Operation(summary = "Créer un patient (ADMIN, MEDECIN)")
    public ResponseEntity<ApiResponse<PatientResponse>> creerPatient(
            @Valid @RequestBody AuthRequest.InscriptionPatientRequest request) {
        var authResponse = authService.inscrirePatient(request);
        var patient = patientService.getPatientParId(authResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Patient créé.", patient));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    @Operation(summary = "Obtenir un patient par ID (ADMIN, MEDECIN)")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Patient trouvé.", patientService.getPatientParId(id)));
    }

    @GetMapping("/moi")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Voir son propre profil (PATIENT)")
    public ResponseEntity<ApiResponse<PatientResponse>> monProfil(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Profil.", patientService.getPatientParEmail(auth.getName())));
    }
}
