package com.project.tesi.repository;

import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // Questo metodo cerca un abbonamento per l'utente che abbia il campo active = true
    Optional<Subscription> findByUserAndIsActiveTrue(User user);

    Optional<Subscription> findByUserIdAndIsActiveTrue(Long userId);
    List<Subscription> findByIsActiveTrue();
    Optional<Subscription> findByUserId(Long userId);
}