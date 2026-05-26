package com.clinique.service;

import com.clinique.entity.RendezVous;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class NotificationService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    public void envoyerRappelConfirmation(RendezVous rendezVous) {
        String patient = rendezVous.getPatient().getPrenom() + " " + rendezVous.getPatient().getNom();
        String medecin = "Dr. " + rendezVous.getMedecin().getPrenom() + " " + rendezVous.getMedecin().getNom();
        String dateHeure = rendezVous.getDateHeure().format(FORMATTER);

        log.info("====================================================");
        log.info("[NOTIFICATION - CONFIRMATION] Envoi simulé par EMAIL & SMS");
        log.info("  → Destinataire : {} ({})", patient, rendezVous.getPatient().getEmail());
        log.info("  → Objet        : ✅ Votre rendez-vous a été confirmé");
        log.info("  → Corps        : Votre rendez-vous avec {} est confirmé pour le {}.", medecin, dateHeure);
        log.info("====================================================");
    }

    public void envoyerRappel24h(RendezVous rendezVous) {
        String patient = rendezVous.getPatient().getPrenom() + " " + rendezVous.getPatient().getNom();
        String medecin = "Dr. " + rendezVous.getMedecin().getPrenom() + " " + rendezVous.getMedecin().getNom();
        String dateHeure = rendezVous.getDateHeure().format(FORMATTER);

        log.info("====================================================");
        log.info("[NOTIFICATION - RAPPEL 24H] Envoi simulé par EMAIL & SMS");
        log.info("  → Destinataire : {} ({})", patient, rendezVous.getPatient().getEmail());
        log.info("  → Objet        : 🔔 Rappel: Rendez-vous demain");
        log.info("  → Corps        : Rappel: Vous avez un rendez-vous avec {} demain le {}.", medecin, dateHeure);
        log.info("====================================================");
    }

    public void envoyerNotificationAnnulation(RendezVous rendezVous) {
        String patient = rendezVous.getPatient().getPrenom() + " " + rendezVous.getPatient().getNom();
        String medecin = "Dr. " + rendezVous.getMedecin().getPrenom() + " " + rendezVous.getMedecin().getNom();
        String dateHeure = rendezVous.getDateHeure().format(FORMATTER);

        log.info("====================================================");
        log.info("[NOTIFICATION - ANNULATION] Envoi simulé par EMAIL & SMS");
        log.info("  → Destinataire : {} ({})", patient, rendezVous.getPatient().getEmail());
        log.info("  → Objet        : ❌ Votre rendez-vous a été annulé");
        log.info("  → Corps        : Votre rendez-vous avec {} du {} a été annulé.", medecin, dateHeure);
        log.info("====================================================");
    }
}
