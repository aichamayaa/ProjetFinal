package com.clinique.repository;

import com.clinique.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByNumeroDossier(String numeroDossier);
    boolean existsByEmail(String email);
    boolean existsByNumeroDossier(String numeroDossier);
}
