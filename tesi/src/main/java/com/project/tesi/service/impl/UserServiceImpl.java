package com.project.tesi.service.impl;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email già registrata");
        }

        User.UserBuilder userBuilder = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword()) // In futuro: passwordEncoder.encode(request.getPassword())
                .role(request.getRole());

        // Logica assegnazione professionisti per i Client durante la registrazione
        if (request.getRole() == Role.CLIENT) {
            assignProfessional(userBuilder, request.getSelectedPtId(), Role.PERSONAL_TRAINER);
            assignProfessional(userBuilder, request.getSelectedNutritionistId(), Role.NUTRITIONIST);
        }

        User savedUser = userRepository.save(userBuilder.build());
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role) {
        return userRepository.findByRole(role).stream()
                .map(pro -> {
                    // Recupera media reale dal DB
                    Double avg = reviewRepository.getAverageRating(pro.getId());
                    long activeClients = userRepository.countByAssignedPT(pro);

                    return ProfessionalSummaryDTO.builder()
                            .id(pro.getId())
                            .fullName(pro.getFirstName() + " " + pro.getLastName())
                            .role(pro.getRole())
                            .averageRating(avg != null ? avg : 0.0) // Usa la media reale o 0.0
                            .currentActiveClients((int) activeClients)
                            .isSoldOut(activeClients >= 50)
                            .build();
                })
                .sorted((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDashboardResponse getClientDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Recuperiamo i professionisti con cui il cliente ha prenotazioni effettive
        List<ProfessionalSummaryDTO> followingProfessionals = bookingRepository.findByUserId(userId)
                .stream()
                .map(booking -> booking.getProfessional())
                .distinct() // Evita duplicati se ci sono più appuntamenti con lo stesso PT
                .map(pro -> ProfessionalSummaryDTO.builder()
                        .id(pro.getId())
                        .fullName(pro.getFirstName() + " " + pro.getLastName())
                        .role(pro.getRole())
                        .build())
                .collect(Collectors.toList());

        return ClientDashboardResponse.builder()
                .profile(mapToUserResponse(user))
                .followingProfessionals(followingProfessionals)
                .build();
    }

    // --- Metodi privati di supporto ---

    private void assignProfessional(User.UserBuilder userBuilder, Long proId, Role expectedRole) {
        if (proId == null) throw new RuntimeException("Devi selezionare un " + expectedRole);

        User professional = userRepository.findById(proId)
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        if (professional.getRole() != expectedRole) {
            throw new RuntimeException("L'ID fornito non corrisponde a un " + expectedRole);
        }

        long activeClients = userRepository.countByAssignedPT(professional);
        if (activeClients >= 50) {
            throw new RuntimeException("Il professionista " + professional.getFirstName() + " è Sold Out.");
        }

        if (expectedRole == Role.PERSONAL_TRAINER) {
            userBuilder.assignedPT(professional);
        } else {
            userBuilder.assignedNutritionist(professional);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        Double avgRating = null;
        Integer clientsCount = null;

        // Se l'utente è un professionista, recuperiamo i dati reali
        if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            // 1. Recupera la media reale dal ReviewRepository
            avgRating = reviewRepository.getAverageRating(user.getId());

            // Se non ci sono ancora recensioni, avgRating sarà null. Possiamo gestirlo mettendo 0.0 o lasciando null.
            if (avgRating == null) avgRating = 0.0;

            // 2. Recupera il conteggio clienti reale
            clientsCount = (int) userRepository.countByAssignedPT(user);
        }

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .assignedPtName(user.getAssignedPT() != null ?
                        user.getAssignedPT().getFirstName() + " " + user.getAssignedPT().getLastName() : null)
                .assignedNutritionistName(user.getAssignedNutritionist() != null ?
                        user.getAssignedNutritionist().getFirstName() + " " + user.getAssignedNutritionist().getLastName() : null)
                .activeClientsCount(clientsCount)
                .averageRating(avgRating) // ORA È REALE
                .build();
    }
}