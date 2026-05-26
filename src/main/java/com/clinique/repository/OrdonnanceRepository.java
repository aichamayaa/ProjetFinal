package com.clinique.repository;

import com.clinique.entity.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {

    List<Ordonnance> findByRendezVousId(Long rendezVousId);

    @Query("SELECT o FROM Ordonnance o WHERE o.rendezVous.patient.id = :patientId")
    List<Ordonnance> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT o FROM Ordonnance o WHERE o.rendezVous.medecin.id = :medecinId")
    List<Ordonnance> findByMedecinId(@Param("medecinId") Long medecinId);
}
