package com.clinique.service;

import com.clinique.dto.response.DisponibilitePlageResponse;
import com.clinique.entity.Medecin;
import com.clinique.entity.RendezVous;
import com.clinique.exception.BusinessException;
import com.clinique.exception.ResourceNotFoundException;
import com.clinique.repository.MedecinRepository;
import com.clinique.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisponibiliteService {

    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;

    public List<DisponibilitePlageResponse> getDisponibilites(Long medecinId, LocalDate date) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + medecinId));

        if (medecin.getHeureDebutTravail() == null || medecin.getHeureFinTravail() == null) {
            throw new BusinessException("Ce médecin n'a pas d'horaire de travail défini.");
        }

        LocalDateTime debutJournee = date.atTime(medecin.getHeureDebutTravail());
        LocalDateTime finJournee = date.atTime(medecin.getHeureFinTravail());
        int duree = medecin.getDureeConsultationMinutes();

        List<RendezVous> rdvExistants = rendezVousRepository.findActiveByMedecinAndDay(
                medecinId, debutJournee, finJournee);

        List<DisponibilitePlageResponse> plages = new ArrayList<>();
        LocalDateTime curseur = debutJournee;

        while (!curseur.plusMinutes(duree).isAfter(finJournee)) {
            LocalDateTime finPlage = curseur.plusMinutes(duree);
            boolean libre = estLibre(curseur, finPlage, rdvExistants);
            plages.add(DisponibilitePlageResponse.builder()
                    .debut(curseur).fin(finPlage)
                    .dureeMinutes(duree).disponible(libre).build());
            curseur = curseur.plusMinutes(duree);
        }

        return plages;
    }

    private boolean estLibre(LocalDateTime debut, LocalDateTime fin, List<RendezVous> rdvExistants) {
        for (RendezVous rdv : rdvExistants) {
            LocalDateTime rdvDebut = rdv.getDateHeure();
            LocalDateTime rdvFin = rdvDebut.plusMinutes(rdv.getDureeMinutes());
            if (debut.isBefore(rdvFin) && fin.isAfter(rdvDebut)) return false;
        }
        return true;
    }

    public void verifierDisponibilite(Long medecinId, LocalDateTime dateHeure, int dureeMinutes, Long excludeRdvId) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + medecinId));

        LocalDateTime finRdv = dateHeure.plusMinutes(dureeMinutes);

        if (medecin.getHeureDebutTravail() != null && medecin.getHeureFinTravail() != null) {
            LocalTime heure = dateHeure.toLocalTime();
            if (heure.isBefore(medecin.getHeureDebutTravail()) || finRdv.toLocalTime().isAfter(medecin.getHeureFinTravail())) {
                throw new BusinessException(String.format(
                        "Le créneau est hors des heures de travail du médecin (%s - %s).",
                        medecin.getHeureDebutTravail(), medecin.getHeureFinTravail()));
            }
        }

        LocalDateTime dayStart = dateHeure.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        List<RendezVous> rdvsDuJour = rendezVousRepository.findActiveByMedecinAndDay(medecinId, dayStart, dayEnd);

        if (excludeRdvId != null) {
            rdvsDuJour.removeIf(rdv -> rdv.getId().equals(excludeRdvId));
        }

        for (RendezVous rdv : rdvsDuJour) {
            LocalDateTime rdvDebut = rdv.getDateHeure();
            LocalDateTime rdvFin = rdvDebut.plusMinutes(rdv.getDureeMinutes());
            if (dateHeure.isBefore(rdvFin) && finRdv.isAfter(rdvDebut)) {
                throw new BusinessException("Conflit de rendez-vous: ce créneau est déjà occupé.");
            }
        }
    }
}
