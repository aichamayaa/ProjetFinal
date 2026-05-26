package com.clinique.service;

import com.clinique.dto.request.MedecinRequest;
import com.clinique.dto.response.MedecinResponse;
import com.clinique.entity.Medecin;
import com.clinique.enums.Role;
import com.clinique.enums.Specialite;
import com.clinique.exception.BusinessException;
import com.clinique.exception.ResourceNotFoundException;
import com.clinique.repository.MedecinRepository;
import com.clinique.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedecinService {

    private final MedecinRepository medecinRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MedecinResponse creerMedecin(MedecinRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Un compte avec cet email existe déjà.");
        }

        if (request.getMotDePasse() == null || request.getMotDePasse().isBlank()) {
            throw new BusinessException("Le mot de passe est obligatoire pour créer un médecin.");
        }

        Medecin medecin = Medecin.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.ROLE_MEDECIN)
                .specialite(request.getSpecialite())
                .heureDebutTravail(request.getHeureDebutTravail())
                .heureFinTravail(request.getHeureFinTravail())
                .dureeConsultationMinutes(request.getDureeConsultationMinutes())
                .build();

        medecinRepository.save(medecin);
        log.info("Nouveau médecin créé: Dr. {} {}", medecin.getPrenom(), medecin.getNom());
        return toResponse(medecin);
    }

    @Transactional(readOnly = true)
    public List<MedecinResponse> listerTousMedecins() {
        return medecinRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedecinResponse getMedecinParId(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'ID: " + id));
        return toResponse(medecin);
    }

    @Transactional(readOnly = true)
    public List<MedecinResponse> getMedecinParSpecialite(Specialite specialite) {
        return medecinRepository.findBySpecialite(specialite).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedecinResponse mettreAJourMedecin(Long id, MedecinRequest request) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'ID: " + id));

        if (!medecin.getEmail().equals(request.getEmail()) &&
            utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé.");
        }

        medecin.setNom(request.getNom());
        medecin.setPrenom(request.getPrenom());
        medecin.setEmail(request.getEmail());
        medecin.setSpecialite(request.getSpecialite());
        medecin.setHeureDebutTravail(request.getHeureDebutTravail());
        medecin.setHeureFinTravail(request.getHeureFinTravail());
        medecin.setDureeConsultationMinutes(request.getDureeConsultationMinutes());

        if (request.getMotDePasse() != null && !request.getMotDePasse().isBlank()) {
            medecin.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }

        return toResponse(medecinRepository.save(medecin));
    }

    @Transactional
    public void desactiverMedecin(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'ID: " + id));
        medecin.setActif(false);
        medecinRepository.save(medecin);
        log.info("Médecin désactivé: {}", id);
    }

    public MedecinResponse toResponse(Medecin medecin) {
        return MedecinResponse.builder()
                .id(medecin.getId())
                .nom(medecin.getNom())
                .prenom(medecin.getPrenom())
                .email(medecin.getEmail())
                .specialite(medecin.getSpecialite())
                .heureDebutTravail(medecin.getHeureDebutTravail())
                .heureFinTravail(medecin.getHeureFinTravail())
                .dureeConsultationMinutes(medecin.getDureeConsultationMinutes())
                .actif(medecin.isActif())
                .build();
    }
}
