package com.project.tesi.facade;

import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;

import java.util.List;

public interface IModeratorFacade {
    List<UserResponseDTO> getManageableUsers();
    List<SubscriptionResponseDTO> getAllSubscriptions();
    List<UserResponseDTO> getChatContacts();
    UserResponseDTO createUser(UserCreateRequestDTO request);
    UserResponseDTO updateUser(Long id, ModeratorUserUpdateRequest request);
    void deleteUser(Long id);
    SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri);
    void closeChat(Long chatId, Long moderatorId);
}
