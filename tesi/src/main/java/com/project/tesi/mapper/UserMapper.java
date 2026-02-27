package com.project.tesi.mapper;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.model.User;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    // Metodo per convertire l'Entity User nel DTO UserResponse
    public UserResponse toUserResponse(User user) {
        Double avgRating = null;
        Integer clientsCount = null;

        if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            avgRating = reviewRepository.getAverageRating(user.getId());
            if (avgRating == null) avgRating = 0.0;
            if (user.getRole() == Role.PERSONAL_TRAINER) {
                clientsCount = (int) userRepository.countByAssignedPT(user);
            } else {
                clientsCount = (int) userRepository.countByAssignedNutritionist(user);
            }
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
                .averageRating(avgRating)
                .build();
    }

    public User toUser(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword()) // In futuro qui potrai iniettare il PasswordEncoder
                .profilePicture(request.getProfilePicture())
                .role(Role.CLIENT) // Forziamo il ruolo
                .build();
    }
}