package com.project.tesi.repository;

import com.project.tesi.model.Review;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByClientIdAndProfessionalId(Long clientId, Long professionalId);

    List<Review> findByProfessionalId(Long professionalId);

    // Calcola direttamente la media voti nel DB
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.professional.id = :profId")
    Double getAverageRating(@Param("profId") Long profId);
    // Serve a ReviewServiceImpl
    List<Review> findByProfessional(User professional);
}