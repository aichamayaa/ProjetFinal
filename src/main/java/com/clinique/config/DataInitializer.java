package com.clinique.config;

import com.clinique.entity.Medecin;
import com.clinique.entity.Utilisateur;
import com.clinique.enums.Role;
import com.clinique.enums.Specialite;
import com.clinique.repository.MedecinRepository;
import com.clinique.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Créer l'admin par défaut si inexistant
        if (!utilisateurRepository.existsByEmail("admin@clinique.com")) {
            Utilisateur admin = Utilisateur.builder()
                    .nom("Admin")
                    .prenom("Super")
                    .email("admin@clinique.com")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            utilisateurRepository.save(admin);
            log.info("✅ Compte ADMIN créé: admin@clinique.com / admin123");
        }

        // Créer deux médecins de démonstration
        if (!medecinRepository.existsByEmail("dr.martin@clinique.com")) {
            Medecin martin = Medecin.builder()
                    .nom("Martin")
                    .prenom("Sophie")
                    .email("dr.martin@clinique.com")
                    .motDePasse(passwordEncoder.encode("medecin123"))
                    .role(Role.ROLE_MEDECIN)
                    .specialite(Specialite.GENERALISTE)
                    .heureDebutTravail(LocalTime.of(8, 0))
                    .heureFinTravail(LocalTime.of(17, 0))
                    .dureeConsultationMinutes(30)
                    .build();
            medecinRepository.save(martin);
            log.info("✅ Médecin créé: dr.martin@clinique.com / medecin123");
        }

        if (!medecinRepository.existsByEmail("dr.dupont@clinique.com")) {
            Medecin dupont = Medecin.builder()
                    .nom("Dupont")
                    .prenom("Jean")
                    .email("dr.dupont@clinique.com")
                    .motDePasse(passwordEncoder.encode("medecin123"))
                    .role(Role.ROLE_MEDECIN)
                    .specialite(Specialite.CARDIOLOGIE)
                    .heureDebutTravail(LocalTime.of(9, 0))
                    .heureFinTravail(LocalTime.of(18, 0))
                    .dureeConsultationMinutes(45)
                    .build();
            medecinRepository.save(dupont);
            log.info("✅ Médecin créé: dr.dupont@clinique.com / medecin123");
        }
    }
}
