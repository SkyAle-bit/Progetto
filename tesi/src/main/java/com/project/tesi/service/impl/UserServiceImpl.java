package com.project.tesi.service.impl;

import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.model.User;
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
    // private final PasswordEncoder passwordEncoder; // Da attivare con Spring Security

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
                .password(request.getPassword()) // .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole());

        // Logica assegnazione professionisti per i Client
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
                    // Mock data: in futuro sostituire con query reali dal DB
                    double avgRating = 4.8;
                    // Nota: Assicurati di aver aggiunto 'countByAssignedPT' nel Repository o usa un valore fisso per ora
                    long activeClients = userRepository.countByAssignedPT(pro);

                    return ProfessionalSummaryDTO.builder()
                            .id(pro.getId())
                            .fullName(pro.getFirstName() + " " + pro.getLastName())
                            .averageRating(avgRating)
                            .currentActiveClients((int) activeClients)
                            .isSoldOut(activeClients >= 50)
                            .build();
                })
                .sorted((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()))
                .collect(Collectors.toList());
    }

    // Metodi privati di supporto
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
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .assignedPtName(user.getAssignedPT() != null ? user.getAssignedPT().getLastName() : null)
                .assignedNutritionistName(user.getAssignedNutritionist() != null ? user.getAssignedNutritionist().getLastName() : null)
                .build();
    }
}