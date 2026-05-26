package com.clinique.repository;

import com.clinique.entity.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
    List<DossierMedical> findByPatientId(Long patientId);
    List<DossierMedical> findByMedecinId(Long medecinId);
    List<DossierMedical> findByPatientIdOrderByDateConsultationDesc(Long patientId);
}
