package com.project.tesi.repository;

import com.project.tesi.model.User;
import com.project.tesi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Trova tutti i professionisti di un certo tipo
    List<User> findByRole(Role role);

    // CRUCIALE: Metodi per contare i clienti attuali (Limite max 10)
    long countByAssignedPT(User pt);

    long countByAssignedNutritionist(User nutritionist);

    // Metodi per trovare i clienti assegnati
    List<User> findByAssignedPT(User pt);

    List<User> findByAssignedNutritionist(User nutritionist);

    List<User> findByRoleIn(List<Object> objects);
}