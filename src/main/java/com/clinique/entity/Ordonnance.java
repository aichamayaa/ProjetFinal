package com.clinique.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordonnances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RendezVous rendezVous;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(length = 2000, nullable = false)
    private String medicaments;

    @Column(length = 1000, nullable = false)
    private String posologie;

    @Column(nullable = false)
    private LocalDate dateValiditeExpiration;

    @Column(length = 500)
    private String instructionsSpeciales;
}
