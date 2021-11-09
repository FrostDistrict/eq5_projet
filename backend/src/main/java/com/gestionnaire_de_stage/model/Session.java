package com.gestionnaire_de_stage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestionnaire_de_stage.enums.TypeSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeSession typeSession;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
