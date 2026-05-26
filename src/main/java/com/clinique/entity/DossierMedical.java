package com.clinique.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dossiers_medicaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Medecin medecin;

    @Column(nullable = false)
    private LocalDateTime dateConsultation;

    @Column(length = 2000)
    private String notesMedecin;

    @Column(length = 1000)
    private String diagnostic;

    @Column(length = 1000)
    private String symptomes;
}
