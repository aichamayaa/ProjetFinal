package com.clinique.entity;

import com.clinique.enums.Specialite;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medecins")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Medecin extends Utilisateur {

    @Enumerated(EnumType.STRING)
    private Specialite specialite;

    private LocalTime heureDebutTravail;
    private LocalTime heureFinTravail;

    @Builder.Default
    private int dureeConsultationMinutes = 30;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<RendezVous> rendezVous = new ArrayList<>();
}
