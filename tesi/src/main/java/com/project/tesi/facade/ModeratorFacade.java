package com.project.tesi.facade;

import com.project.tesi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> getManageableUsers() {
        return adminService.getModeratorManageableUsers();
    }

    public Map<String, Object> createUser(Map<String, Object> body) {
        return adminService.createUserAsModerator(body);
    }

    public Map<String, Object> updateUser(Long id, Map<String, Object> body) {
        return adminService.updateUserAsModerator(id, body);
    }

    public void deleteUser(Long id) {
        adminService.deleteUserAsModerator(id);
    }
}

