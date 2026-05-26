package com.clinique.repository;

import com.clinique.entity.RendezVous;
import com.clinique.enums.RendezVousStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByMedecinId(Long medecinId);

    List<RendezVous> findByPatientId(Long patientId);

    List<RendezVous> findByMedecinIdAndStatut(Long medecinId, RendezVousStatut statut);

    List<RendezVous> findByPatientIdAndStatut(Long patientId, RendezVousStatut statut);

    @Query("SELECT rv FROM RendezVous rv WHERE rv.medecin.id = :medecinId " +
           "AND rv.dateHeure >= :debut AND rv.dateHeure < :fin " +
           "AND rv.statut <> com.clinique.enums.RendezVousStatut.ANNULE")
    List<RendezVous> findByMedecinIdAndDateHeureBetween(
            @Param("medecinId") Long medecinId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT rv FROM RendezVous rv WHERE rv.medecin.id = :medecinId " +
           "AND rv.statut <> com.clinique.enums.RendezVousStatut.ANNULE " +
           "AND rv.dateHeure >= :dayStart AND rv.dateHeure < :dayEnd")
    List<RendezVous> findActiveByMedecinAndDay(
            @Param("medecinId") Long medecinId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    @Query("SELECT rv FROM RendezVous rv WHERE rv.statut = com.clinique.enums.RendezVousStatut.CONFIRME " +
           "AND rv.dateHeure BETWEEN :debut AND :fin")
    List<RendezVous> findRendezVousAVenir(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
}
