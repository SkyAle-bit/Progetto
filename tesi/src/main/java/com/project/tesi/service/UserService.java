package com.project.tesi.service;

import com.project.tesi.enums.Role;
import com.project.tesi.model.User;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public List<User> findAvailableProfessionals(Role role) {
        // 1. Recupera tutti i professionisti di quel ruolo (PT o Nutrizionista)
        List<User> allPros = userRepository.findByRole(role);

        // 2. Filtra quelli che hanno già 50 clienti e Ordina per Ranking
        return allPros.stream()
                .filter(pro -> {
                    long currentClients = (role == Role.PERSONAL_TRAINER)
                            ? userRepository.countByAssignedPT(pro)
                            : userRepository.countByAssignedNutritionist(pro);
                    return currentClients < 50; // HARD LIMIT
                })
                .sorted(Comparator.comparingDouble(this::calculateAverageRating).reversed()) // Ordine decrescente
                .collect(Collectors.toList());
    }

    private Double calculateAverageRating(User pro) {
        Double avg = reviewRepository.getAverageRating(pro.getId());
        return avg != null ? avg : 0.0;
    }

    // Metodo per assegnare il professionista all'utente (chiamato alla registrazione)
    public void assignProfessionalToClient(User client, Long professionalId, Role proRole) {
        User pro = userRepository.findById(professionalId).orElseThrow();

        // Ricontrollo critico concorrenza (se nel frattempo si è riempito)
        long currentClients = (proRole == Role.PERSONAL_TRAINER) ? userRepository.countByAssignedPT(pro) : userRepository.countByAssignedNutritionist(pro);
        if (currentClients >= 50) {
            throw new IllegalStateException("Questo professionista ha raggiunto il limite massimo di clienti.");
        }

        if (proRole == Role.PERSONAL_TRAINER) {
            client.setAssignedPT(pro);
        } else {
            client.setAssignedNutritionist(pro);
        }
        userRepository.save(client);
    }
}