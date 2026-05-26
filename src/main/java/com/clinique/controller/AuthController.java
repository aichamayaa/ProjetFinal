package com.clinique.controller;

import com.clinique.dto.request.AuthRequest;
import com.clinique.dto.response.ApiResponse;
import com.clinique.dto.response.AuthResponse;
import com.clinique.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/inscription")
    @Operation(summary = "Inscription d'un nouveau patient")
    public ResponseEntity<ApiResponse<AuthResponse>> inscrirePatient(
            @Valid @RequestBody AuthRequest.InscriptionPatientRequest request) {
        AuthResponse response = authService.inscrirePatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inscription réussie.", response));
    }

    @PostMapping("/connexion")
    @Operation(summary = "Connexion et obtention du token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> connecter(
            @Valid @RequestBody AuthRequest.LoginRequest request) {
        AuthResponse response = authService.connecter(request);
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie.", response));
    }
}
