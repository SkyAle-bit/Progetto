package com.project.tesi.repository;

import com.project.tesi.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Trova l'abbonamento attivo di un utente (per scalare crediti)
    Optional<Subscription> findBySubscriberIdAndIsActiveTrue(Long userId);

    // Serve allo Scheduler notturno per rinnovare crediti/rate
    List<Subscription> findByIsActiveTrue();
}