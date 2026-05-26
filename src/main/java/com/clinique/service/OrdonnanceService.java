package com.clinique.service;

import com.clinique.dto.request.OrdonnanceRequest;
import com.clinique.dto.response.OrdonnanceResponse;
import com.clinique.entity.Medecin;
import com.clinique.entity.Ordonnance;
import com.clinique.entity.RendezVous;
import com.clinique.enums.RendezVousStatut;
import com.clinique.exception.BusinessException;
import com.clinique.exception.ResourceNotFoundException;
import com.clinique.repository.MedecinRepository;
import com.clinique.repository.OrdonnanceRepository;
import com.clinique.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;
    private final MedecinService medecinService;
    private final PatientService patientService;

    @Transactional
    public OrdonnanceResponse creerOrdonnance(OrdonnanceRequest request, String emailMedecin) {
        RendezVous rdv = rendezVousRepository.findById(request.getRendezVousId())
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable: " + request.getRendezVousId()));

        if (!rdv.getMedecin().getEmail().equals(emailMedecin)) {
            throw new AccessDeniedException("Vous ne pouvez créer une ordonnance que pour vos propres rendez-vous.");
        }

        if (rdv.getStatut() == RendezVousStatut.ANNULE) {
            throw new BusinessException("Impossible de créer une ordonnance pour un rendez-vous annulé.");
        }

        Ordonnance ordonnance = Ordonnance.builder()
                .rendezVous(rdv)
                .dateCreation(LocalDateTime.now())
                .medicaments(request.getMedicaments())
                .posologie(request.getPosologie())
                .dateValiditeExpiration(request.getDateValiditeExpiration())
                .instructionsSpeciales(request.getInstructionsSpeciales())
                .build();

        return toResponse(ordonnanceRepository.save(ordonnance));
    }

    @Transactional(readOnly = true)
    public List<OrdonnanceResponse> getMesOrdonnances(String emailPatient) {
        var patient = patientService.getPatientEntityParEmail(emailPatient);
        return ordonnanceRepository.findByPatientId(patient.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdonnanceResponse> getOrdonnancesParMedecin(String emailMedecin) {
        Medecin medecin = medecinRepository.findByEmail(emailMedecin)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + emailMedecin));
        return ordonnanceRepository.findByMedecinId(medecin.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrdonnanceResponse getOrdonnanceParId(Long id, String email, boolean isMedecin, boolean isAdmin) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordonnance introuvable: " + id));

        if (!isAdmin && !isMedecin && !ordonnance.getRendezVous().getPatient().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès refusé à cette ordonnance.");
        }

        return toResponse(ordonnance);
    }

    @Transactional(readOnly = true)
    public List<OrdonnanceResponse> getOrdonnancesParRendezVous(Long rendezVousId, String emailMedecin) {
        RendezVous rdv = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous introuvable: " + rendezVousId));

        if (!rdv.getMedecin().getEmail().equals(emailMedecin)) {
            throw new AccessDeniedException("Accès refusé.");
        }

        return ordonnanceRepository.findByRendezVousId(rendezVousId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdonnanceResponse> getToutesOrdonnances() {
        return ordonnanceRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private OrdonnanceResponse toResponse(Ordonnance o) {
        return OrdonnanceResponse.builder()
                .id(o.getId())
                .rendezVousId(o.getRendezVous().getId())
                .dateCreation(o.getDateCreation())
                .medicaments(o.getMedicaments())
                .posologie(o.getPosologie())
                .dateValiditeExpiration(o.getDateValiditeExpiration())
                .instructionsSpeciales(o.getInstructionsSpeciales())
                .medecin(medecinService.toResponse(o.getRendezVous().getMedecin()))
                .patient(patientService.toResponse(o.getRendezVous().getPatient()))
                .build();
    }
}
