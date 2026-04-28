package com.project.tesi.facade;

import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Facade per il pannello moderatore.
 *
 * Espone solo la gestione utenti consentita al moderatore
 * (clienti, personal trainer e nutrizionisti).
 */
@Component
@RequiredArgsConstructor
public class ModeratorFacade {

    private final AdminService adminService;

    public List<UserResponseDTO> getManageableUsers() {
        return adminService.getModeratorManageableUsers().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return adminService.getAllSubscriptions().stream()
                .map(this::mapToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> getChatContacts() {
        return adminService.getModeratorChatContacts().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(Map<String, Object> body) {
        return mapToUserResponse(adminService.createUserAsModerator(body));
    }

    public UserResponseDTO updateUser(Long id, Map<String, Object> body) {
        return mapToUserResponse(adminService.updateUserAsModerator(id, body));
    }

    public void deleteUser(Long id) {
        adminService.deleteUserAsModerator(id);
    }

    public SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri) {
        return mapToSubscriptionResponse(adminService.updateSubscriptionCredits(id, pt, nutri));
    }

    private UserResponseDTO mapToUserResponse(Map<String, Object> map) {
        return new UserResponseDTO(
                map.get("id") != null ? Long.parseLong(map.get("id").toString()) : null,
                (String) map.get("firstName"),
                (String) map.get("lastName"),
                (String) map.get("email"),
                (String) map.get("role"),
                (String) map.get("createdAt"),
                (String) map.get("professionalBio"),
                (String) map.get("assignedPTName"),
                (String) map.get("assignedNutritionistName")
        );
    }

    private SubscriptionResponseDTO mapToSubscriptionResponse(Map<String, Object> map) {
        return new SubscriptionResponseDTO(
                map.get("id") != null ? Long.parseLong(map.get("id").toString()) : null,
                map.get("userId") != null ? Long.parseLong(map.get("userId").toString()) : null,
                (String) map.get("userName"),
                (String) map.get("planName"),
                map.get("active") != null ? (Boolean) map.get("active") : false,
                (String) map.get("startDate"),
                (String) map.get("endDate"),
                map.get("monthlyPrice") != null ? Double.parseDouble(map.get("monthlyPrice").toString()) : 0.0,
                map.get("currentCreditsPT") != null ? Integer.parseInt(map.get("currentCreditsPT").toString()) : 0,
                map.get("currentCreditsNutri") != null ? Integer.parseInt(map.get("currentCreditsNutri").toString()) : 0
        );
    }
}
