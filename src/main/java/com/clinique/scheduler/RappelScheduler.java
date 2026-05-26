package com.clinique.scheduler;

import com.clinique.entity.RendezVous;
import com.clinique.repository.RendezVousRepository;
import com.clinique.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RappelScheduler {

    private final RendezVousRepository rendezVousRepository;
    private final NotificationService notificationService;

    /**
     * Vérifie toutes les heures les rendez-vous dans les 24-25 heures à venir
     * et envoie un rappel simulé.
     */
    @Scheduled(fixedRate = 3600000) // chaque heure
    public void envoyerRappels24h() {
        LocalDateTime dans24h = LocalDateTime.now().plusHours(24);
        LocalDateTime dans25h = LocalDateTime.now().plusHours(25);

        List<RendezVous> rdvsAVenir = rendezVousRepository.findRendezVousAVenir(dans24h, dans25h);

        if (rdvsAVenir.isEmpty()) {
            log.debug("[SCHEDULER] Aucun rappel 24h à envoyer.");
            return;
        }

        log.info("[SCHEDULER] Envoi de {} rappel(s) 24h...", rdvsAVenir.size());
        for (RendezVous rdv : rdvsAVenir) {
            notificationService.envoyerRappel24h(rdv);
        }
    }
}
