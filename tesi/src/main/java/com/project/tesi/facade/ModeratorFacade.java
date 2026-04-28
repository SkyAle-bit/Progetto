package com.project.tesi.facade;

import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.service.AdminService;
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
public class ModeratorFacade {

    private final AdminService adminService;

    // Costruttore esplicito — pattern Facade
    public ModeratorFacade(AdminService adminService) {
        this.adminService = adminService;
    }

    public List<UserResponseDTO> getManageableUsers() {
        return adminService.getModeratorManageableUsers().stream()
                .map(FacadeMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return adminService.getAllSubscriptions().stream()
                .map(FacadeMapper::mapToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> getChatContacts() {
        return adminService.getModeratorChatContacts().stream()
                .map(FacadeMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        return FacadeMapper.mapToUserResponse(adminService.createUserAsModerator(request));
    }

    public UserResponseDTO updateUser(Long id, Map<String, Object> body) {
        return FacadeMapper.mapToUserResponse(adminService.updateUserAsModerator(id, body));
    }

    public void deleteUser(Long id) {
        adminService.deleteUserAsModerator(id);
    }

    public SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri) {
        return FacadeMapper.mapToSubscriptionResponse(adminService.updateSubscriptionCredits(id, pt, nutri));
    }
}
