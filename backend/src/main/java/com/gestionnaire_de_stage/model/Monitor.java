package com.gestionnaire_de_stage.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Monitor extends User {

    @NotNull
    private String department;

    private String address;

    private String city;

    private String postalCode;
}
