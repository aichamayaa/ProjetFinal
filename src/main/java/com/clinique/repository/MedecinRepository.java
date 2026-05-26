package com.clinique.repository;

import com.clinique.entity.Medecin;
import com.clinique.enums.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Optional<Medecin> findByEmail(String email);
    List<Medecin> findBySpecialite(Specialite specialite);
    boolean existsByEmail(String email);
}
