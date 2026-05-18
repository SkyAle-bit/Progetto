package com.project.tesi.facade.impl;

import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.facade.FacadeMapper;
import com.project.tesi.facade.IModeratorFacade;
import com.project.tesi.service.AdminService;
import com.project.tesi.service.ChatService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del facade per il pannello del moderatore.
 */
@Component
public class ModeratorFacadeImpl implements IModeratorFacade {

    private final AdminService adminService;
    private final ChatService chatService;
    private final FacadeMapper facadeMapper;

    public ModeratorFacadeImpl(AdminService adminService, ChatService chatService, FacadeMapper facadeMapper) {
        this.adminService = adminService;
        this.chatService = chatService;
        this.facadeMapper = facadeMapper;
    }

    @Override
    public List<UserResponseDTO> getManageableUsers() {
        return adminService.getModeratorManageableUsers().stream()
                .map(facadeMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return adminService.getAllSubscriptions().stream()
                .map(facadeMapper::mapToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getChatContacts() {
        return adminService.getModeratorChatContacts().stream()
                .map(facadeMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        return facadeMapper.mapToUserResponse(adminService.createUserAsModerator(request));
    }

    @Override
    public UserResponseDTO updateUser(Long id, ModeratorUserUpdateRequest request) {
        return facadeMapper.mapToUserResponse(adminService.updateUserAsModerator(id, request));
    }

    @Override
    public void deleteUser(Long id) {
        adminService.deleteUserAsModerator(id);
    }

    @Override
    public SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri) {
        return facadeMapper.mapToSubscriptionResponse(adminService.updateSubscriptionCredits(id, pt, nutri));
    }

    @Override
    public void closeChat(Long chatId, Long moderatorId) {
        chatService.closeChat(chatId, moderatorId);
    }
}
