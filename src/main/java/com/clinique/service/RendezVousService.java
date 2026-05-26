package com.clinique.service;

import com.clinique.dto.request.RendezVousRequest;
import com.clinique.dto.response.RendezVousResponse;
import com.clinique.entity.Medecin;
import com.clinique.entity.Patient;
import com.clinique.entity.RendezVous;
import com.clinique.enums.RendezVousStatut;
import com.clinique.enums.Role;
import com.clinique.exception.BusinessException;
import com.clinique.exception.ResourceNotFoundException;
import com.clinique.repository.MedecinRepository;
import com.clinique.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;
    private final PatientService patientService;
    private final MedecinService medecinService;
    private final DisponibiliteService disponibiliteService;
    private final NotificationService notificationService;

    @Transactional
    public RendezVousResponse prendreRendezVous(RendezVousRequest request, String emailPatient) {
        Patient patient = patientService.getPatientEntityParEmail(emailPatient);
        Medecin medecin = medecinRepository.findById(request.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + request.getMedecinId()));

        disponibiliteService.verifierDisponibilite(
                medecin.getId(), request.getDateHeure(), request.getDureeMinutes(), null);

        RendezVous rdv = RendezVous.builder()
                .dateHeure(request.getDateHeure())
                .dureeMinutes(request.getDureeMinutes() > 0 ? request.getDureeMinutes() : medecin.getDureeConsultationMinutes())
                .statut(RendezVousStatut.PLANIFIE)
                .motif(request.getMotif())
                .medecin(medecin)
                .patient(patient)
                .build();

        rdv = rendezVousRepository.save(rdv);
        log.info("Rendez-vous créé: ID={}, Patient={}, Médecin={}, Date={}",
                rdv.getId(), patient.getEmail(), medecin.getNom(), rdv.getDateHeure());

        return toResponse(rdv);
    }

    @Transactional
    public RendezVousResponse prendreRendezVousAdmin(RendezVousRequest request) {
        if (request.getPatientId() == null) {
            throw new BusinessException("L'ID du patient est obligatoire pour l'admin.");
        }
        Patient patient = patientService.getPatientEntity(request.getPatientId());
        Medecin medecin = medecinRepository.findById(request.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + request.getMedecinId()));

        disponibiliteService.verifierDisponibilite(
                medecin.getId(), request.getDateHeure(), request.getDureeMinutes(), null);

        RendezVous rdv = RendezVous.builder()
                .dateHeure(request.getDateHeure())
                .dureeMinutes(request.getDureeMinutes() > 0 ? request.getDureeMinutes() : medecin.getDureeConsultationMinutes())
                .statut(RendezVousStatut.PLANIFIE)
                .motif(request.getMotif())
                .medecin(medecin)
                .patient(patient)
                .build();

        return toResponse(rendezVousRepository.save(rdv));
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponse> getMesRendezVous(String email, Role role) {
        if (role == Role.ROLE_PATIENT) {
            Patient patient = patientService.getPatientEntityParEmail(email);
            return rendezVousRepository.findByPatientId(patient.getId())
                    .stream().map(this::toResponse).collect(Collectors.toList());
        } else if (role == Role.ROLE_MEDECIN) {
            Medecin medecin = medecinRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + email));
            return rendezVousRepository.findByMedecinId(medecin.getId())
                    .stream().map(this::toResponse).collect(Collectors.toList());
        }
        return rendezVousRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponse> getTousRendezVous() {
        return rendezVousRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RendezVousResponse getRendezVousParId(Long id, String email, Role role) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable: " + id));

        if (role == Role.ROLE_PATIENT && !rdv.getPatient().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès refusé à ce rendez-vous.");
        }
        if (role == Role.ROLE_MEDECIN && !rdv.getMedecin().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès refusé à ce rendez-vous.");
        }

        return toResponse(rdv);
    }

    @Transactional
    public RendezVousResponse confirmerRendezVous(Long id, String emailActeur) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable: " + id));

        // Admin can confirm any RDV; médecin can only confirm their own
        boolean isMedecinTraitant = rdv.getMedecin().getEmail().equals(emailActeur);
        boolean isAdmin = !medecinRepository.existsByEmail(emailActeur);

        if (!isMedecinTraitant && !isAdmin) {
            throw new AccessDeniedException("Vous ne pouvez confirmer que vos propres rendez-vous.");
        }
        if (rdv.getStatut() != RendezVousStatut.PLANIFIE) {
            throw new BusinessException("Seul un rendez-vous PLANIFIÉ peut être confirmé.");
        }

        rdv.setStatut(RendezVousStatut.CONFIRME);
        rdv = rendezVousRepository.save(rdv);

        notificationService.envoyerRappelConfirmation(rdv);

        return toResponse(rdv);
    }

    @Transactional
    public RendezVousResponse annulerRendezVous(Long id, String email, Role role) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable: " + id));

        if (role == Role.ROLE_PATIENT && !rdv.getPatient().getEmail().equals(email)) {
            throw new AccessDeniedException("Vous ne pouvez annuler que vos propres rendez-vous.");
        }

        if (rdv.getStatut() == RendezVousStatut.ANNULE || rdv.getStatut() == RendezVousStatut.TERMINE) {
            throw new BusinessException("Ce rendez-vous ne peut pas être annulé (statut: " + rdv.getStatut() + ").");
        }

        rdv.setStatut(RendezVousStatut.ANNULE);
        rdv = rendezVousRepository.save(rdv);

        notificationService.envoyerNotificationAnnulation(rdv);

        return toResponse(rdv);
    }

    @Transactional
    public RendezVousResponse terminerRendezVous(Long id, String emailActeur) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable: " + id));

        boolean isMedecinTraitant = rdv.getMedecin().getEmail().equals(emailActeur);
        boolean isAdmin = !medecinRepository.existsByEmail(emailActeur);

        if (!isMedecinTraitant && !isAdmin) {
            throw new AccessDeniedException("Vous ne pouvez terminer que vos propres rendez-vous.");
        }
        if (rdv.getStatut() != RendezVousStatut.CONFIRME) {
            throw new BusinessException("Seul un rendez-vous CONFIRMÉ peut être marqué comme terminé.");
        }

        rdv.setStatut(RendezVousStatut.TERMINE);
        return toResponse(rendezVousRepository.save(rdv));
    }

    public RendezVousResponse toResponse(RendezVous rdv) {
        return RendezVousResponse.builder()
                .id(rdv.getId())
                .dateHeure(rdv.getDateHeure())
                .dureeMinutes(rdv.getDureeMinutes())
                .statut(rdv.getStatut())
                .motif(rdv.getMotif())
                .medecin(medecinService.toResponse(rdv.getMedecin()))
                .patient(patientService.toResponse(rdv.getPatient()))
                .build();
    }
}
