package com.project.tesi.facade;

import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.service.AdminService;
import com.project.tesi.service.ChatService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Facade per il pannello del moderatore.
 * Espone solo un sottoinsieme sicuro dei metodi di amministrazione.
 */
@Component
public class ModeratorFacade {

    private final AdminService adminService;
    private final ChatService chatService;

    public ModeratorFacade(AdminService adminService, ChatService chatService) {
        this.adminService = adminService;
        this.chatService = chatService;
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

    public UserResponseDTO updateUser(Long id, ModeratorUserUpdateRequest request) {
        return FacadeMapper.mapToUserResponse(adminService.updateUserAsModerator(id, request));
    }

    public void deleteUser(Long id) {
        adminService.deleteUserAsModerator(id);
    }

    public SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri) {
        return FacadeMapper.mapToSubscriptionResponse(adminService.updateSubscriptionCredits(id, pt, nutri));
    }

    public void closeChat(Long chatId, Long moderatorId) {
        chatService.closeChat(chatId, moderatorId);
    }
}
