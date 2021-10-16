package com.gestionnaire_de_stage.repository;

import com.gestionnaire_de_stage.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Manager findManagerByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    boolean existsByEmailAndPassword(String email, String password);
}
