package com.clinique.service;

import com.clinique.dto.request.AuthRequest;
import com.clinique.dto.response.AuthResponse;
import com.clinique.entity.Patient;
import com.clinique.entity.Utilisateur;
import com.clinique.enums.Role;
import com.clinique.exception.BusinessException;
import com.clinique.repository.PatientRepository;
import com.clinique.repository.UtilisateurRepository;
import com.clinique.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse inscrirePatient(AuthRequest.InscriptionPatientRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Un compte avec cet email existe déjà.");
        }

        Patient patient = Patient.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.ROLE_PATIENT)
                .telephone(request.getTelephone())
                .dateNaissance(request.getDateNaissance())
                .numeroDossier(genererNumeroDossier())
                .build();

        patientRepository.save(patient);
        log.info("Nouveau patient inscrit: {} {}", patient.getPrenom(), patient.getNom());

        String token = jwtService.generateToken(patient);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(patient.getId())
                .email(patient.getEmail())
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .role(patient.getRole())
                .build();
    }

    public AuthResponse connecter(AuthRequest.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Identifiants invalides."));

        String token = jwtService.generateToken(utilisateur);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole())
                .build();
    }

    private String genererNumeroDossier() {
        String numero;
        do {
            numero = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (patientRepository.existsByNumeroDossier(numero));
        return numero;
    }
}
