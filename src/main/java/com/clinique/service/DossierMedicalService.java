package com.clinique.service;

import com.clinique.dto.request.DossierMedicalRequest;
import com.clinique.dto.response.DossierMedicalResponse;
import com.clinique.entity.DossierMedical;
import com.clinique.entity.Medecin;
import com.clinique.entity.Patient;
import com.clinique.exception.ResourceNotFoundException;
import com.clinique.repository.DossierMedicalRepository;
import com.clinique.repository.MedecinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DossierMedicalService {

    private final DossierMedicalRepository dossierMedicalRepository;
    private final MedecinRepository medecinRepository;
    private final PatientService patientService;
    private final MedecinService medecinService;

    @Transactional
    public DossierMedicalResponse creerDossier(DossierMedicalRequest request, String emailMedecin) {
        Medecin medecin = medecinRepository.findByEmail(emailMedecin)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + emailMedecin));
        Patient patient = patientService.getPatientEntity(request.getPatientId());

        DossierMedical dossier = DossierMedical.builder()
                .patient(patient)
                .medecin(medecin)
                .dateConsultation(LocalDateTime.now())
                .diagnostic(request.getDiagnostic())
                .notesMedecin(request.getNotesMedecin())
                .symptomes(request.getSymptomes())
                .build();

        return toResponse(dossierMedicalRepository.save(dossier));
    }

    @Transactional(readOnly = true)
    public List<DossierMedicalResponse> getDossiersParPatient(Long patientId) {
        return dossierMedicalRepository.findByPatientIdOrderByDateConsultationDesc(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DossierMedicalResponse getDossierParId(Long id, String email, boolean isMedecin, boolean isAdmin) {
        DossierMedical dossier = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical introuvable: " + id));

        if (!isAdmin && !isMedecin && !dossier.getPatient().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès refusé à ce dossier médical.");
        }

        return toResponse(dossier);
    }

    @Transactional(readOnly = true)
    public List<DossierMedicalResponse> getMesDossiers(String emailPatient) {
        Patient patient = patientService.getPatientEntityParEmail(emailPatient);
        return dossierMedicalRepository.findByPatientIdOrderByDateConsultationDesc(patient.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DossierMedicalResponse> getDossiersParMedecin(String emailMedecin) {
        Medecin medecin = medecinRepository.findByEmail(emailMedecin)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable: " + emailMedecin));
        return dossierMedicalRepository.findByMedecinId(medecin.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DossierMedicalResponse> getTousDossiers() {
        return dossierMedicalRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private DossierMedicalResponse toResponse(DossierMedical d) {
        return DossierMedicalResponse.builder()
                .id(d.getId())
                .patient(patientService.toResponse(d.getPatient()))
                .medecin(medecinService.toResponse(d.getMedecin()))
                .dateConsultation(d.getDateConsultation())
                .notesMedecin(d.getNotesMedecin())
                .diagnostic(d.getDiagnostic())
                .symptomes(d.getSymptomes())
                .build();
    }
}
