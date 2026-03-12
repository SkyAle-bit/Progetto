package com.project.tesi.controller;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.request.ProfileUpdateRequest;
import com.project.tesi.dto.response.*;
import com.project.tesi.enums.Role;
import com.project.tesi.facade.AdminFacade;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.service.ChatService;
import com.project.tesi.service.DocumentService;
import com.project.tesi.service.EmailService;
import com.project.tesi.model.Document;
import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.request.JobApplicationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test unitari per tutti i controller rimanenti.
 */
@ExtendWith(MockitoExtension.class)
class AllControllersTest {

    // ═══════════════════════════════════════════════════════════
    // ADMIN CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class AdminControllerTests {
        @Mock private AdminFacade adminFacade;
        @InjectMocks private AdminController adminController;

        @Test @DisplayName("getAllUsers")
        void getAllUsers() {
            when(adminFacade.getAllUsers()).thenReturn(List.of(Map.of("id", 1L)));
            ResponseEntity<List<Map<String, Object>>> resp = adminController.getAllUsers();
            assertThat(resp.getStatusCode().value()).isEqualTo(200);
            assertThat(resp.getBody()).hasSize(1);
        }

        @Test @DisplayName("createUser")
        void createUser() {
            Map<String, Object> body = Map.of("email", "test@test.com");
            when(adminFacade.createUser(body)).thenReturn(Map.of("id", 1L));
            ResponseEntity<Map<String, Object>> resp = adminController.createUser(body);
            assertThat(resp.getBody().get("id")).isEqualTo(1L);
        }

        @Test @DisplayName("deleteUser")
        void deleteUser() {
            ResponseEntity<Map<String, String>> resp = adminController.deleteUser(1L);
            verify(adminFacade).deleteUser(1L);
            assertThat(resp.getBody().get("message")).contains("eliminato");
        }

        @Test @DisplayName("getAllSubscriptions")
        void getAllSubscriptions() {
            when(adminFacade.getAllSubscriptions()).thenReturn(List.of());
            assertThat(adminController.getAllSubscriptions().getBody()).isEmpty();
        }

        @Test @DisplayName("createPlan")
        void createPlan() {
            Map<String, Object> body = Map.of("name", "Premium");
            when(adminFacade.createPlan(body)).thenReturn(Map.of("id", 1L));
            assertThat(adminController.createPlan(body).getBody().get("id")).isEqualTo(1L);
        }

        @Test @DisplayName("deletePlan")
        void deletePlan() {
            ResponseEntity<Map<String, String>> resp = adminController.deletePlan(1L);
            verify(adminFacade).deletePlan(1L);
            assertThat(resp.getBody().get("message")).contains("eliminato");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ADMIN STATS CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class AdminStatsControllerTests {
        @Mock private AdminFacade adminFacade;
        @InjectMocks private AdminStatsController adminStatsController;

        @Test @DisplayName("getStats")
        void getStats() {
            when(adminFacade.getAdminStats()).thenReturn(Map.of("totalUsers", 50));
            ResponseEntity<Map<String, Object>> resp = adminStatsController.getStats();
            assertThat(resp.getBody().get("totalUsers")).isEqualTo(50);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // USER CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class UserControllerTests {
        @Mock private UserFacade userFacade;
        @InjectMocks private UserController userController;

        @Test @DisplayName("getDashboard")
        void getDashboard() {
            ClientDashboardResponse dash = ClientDashboardResponse.builder().build();
            when(userFacade.getClientDashboard(1L)).thenReturn(dash);
            assertThat(userController.getDashboard(1L).getBody()).isEqualTo(dash);
        }

        @Test @DisplayName("getClientsForProfessional")
        void getClientsForProfessional() {
            when(userFacade.getClientsForProfessional(2L)).thenReturn(List.of());
            assertThat(userController.getClientsForProfessional(2L).getBody()).isEmpty();
        }

        @Test @DisplayName("updateProfile")
        void updateProfile() {
            ProfileUpdateRequest req = new ProfileUpdateRequest();
            ResponseEntity<Void> resp = userController.updateProfile(1L, req);
            verify(userFacade).updateProfile(1L, req);
            assertThat(resp.getStatusCode().value()).isEqualTo(200);
        }

        @Test @DisplayName("getAdmin")
        void getAdmin() {
            ClientBasicInfoResponse admin = ClientBasicInfoResponse.builder().id(99L).build();
            when(userFacade.getAdmin()).thenReturn(admin);
            assertThat(userController.getAdmin().getBody().getId()).isEqualTo(99L);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // SUBSCRIPTION CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class SubscriptionControllerTests {
        @Mock private UserFacade userFacade;
        @InjectMocks private SubscriptionController subscriptionController;

        @Test @DisplayName("activateSubscription")
        void activateSubscription() {
            PlanRequest req = new PlanRequest();
            SubscriptionResponse resp = SubscriptionResponse.builder().id(1L).isActive(true).build();
            when(userFacade.activateSubscription(req)).thenReturn(resp);
            assertThat(subscriptionController.activateSubscription(req).getBody().isActive()).isTrue();
        }

        @Test @DisplayName("getSubscriptionStatus")
        void getSubscriptionStatus() {
            SubscriptionResponse resp = SubscriptionResponse.builder().id(1L).build();
            when(userFacade.getSubscriptionStatus(1L)).thenReturn(resp);
            assertThat(subscriptionController.getSubscriptionStatus(1L).getBody().getId()).isEqualTo(1L);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PROFESSIONAL CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class ProfessionalControllerTests {
        @Mock private UserFacade userFacade;
        @InjectMocks private ProfessionalController professionalController;

        @Test @DisplayName("getProfessionals")
        void getProfessionals() {
            when(userFacade.findAvailableProfessionals(Role.PERSONAL_TRAINER)).thenReturn(List.of());
            assertThat(professionalController.getProfessionals(Role.PERSONAL_TRAINER).getBody()).isEmpty();
        }

        @Test @DisplayName("getProfessionalSlots")
        void getProfessionalSlots() {
            when(userFacade.getAvailableSlots(2L)).thenReturn(List.of());
            assertThat(professionalController.getProfessionalSlots(2L).getBody()).isEmpty();
        }

        @Test @DisplayName("createSlots")
        void createSlots() {
            List<SlotDTO> slots = List.of();
            when(userFacade.createSlots(2L, slots)).thenReturn(List.of());
            assertThat(professionalController.createSlots(2L, slots).getBody()).isEmpty();
        }

        @Test @DisplayName("deleteSlot")
        void deleteSlot() {
            ResponseEntity<Void> resp = professionalController.deleteSlot(2L, 10L);
            verify(userFacade).deleteSlot(10L);
            assertThat(resp.getStatusCode().value()).isEqualTo(204);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // PROFESSIONAL STATS CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class ProfessionalStatsControllerTests {
        @Mock private UserFacade userFacade;
        @InjectMocks private ProfessionalStatsController professionalStatsController;

        @Test @DisplayName("getStats")
        void getStats() {
            when(userFacade.getProfessionalStats(2L)).thenReturn(Map.of("clients", 5));
            assertThat(professionalStatsController.getStats(2L).getBody().get("clients")).isEqualTo(5);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ACTIVITY FEED CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class ActivityFeedControllerTests {
        @Mock private UserFacade userFacade;
        @InjectMocks private ActivityFeedController activityFeedController;

        @Test @DisplayName("getActivityFeed")
        void getActivityFeed() {
            when(userFacade.getActivityFeed(1L, 14, 15)).thenReturn(List.of(Map.of("type", "booking")));
            ResponseEntity<List<Map<String, Object>>> resp = activityFeedController.getActivityFeed(1L, 14, 15);
            assertThat(resp.getBody()).hasSize(1);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CHAT CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class ChatControllerTests {
        @Mock private ChatService chatService;
        @InjectMocks private ChatController chatController;

        @Test @DisplayName("sendMessage")
        void sendMessage() {
            SendMessageRequest req = new SendMessageRequest();
            ChatMessageResponse resp = ChatMessageResponse.builder().id(1L).build();
            when(chatService.sendMessage(req)).thenReturn(resp);
            assertThat(chatController.sendMessage(req).getBody().getId()).isEqualTo(1L);
        }

        @Test @DisplayName("getConversation")
        void getConversation() {
            when(chatService.getConversation(1L, 2L, 0, 50)).thenReturn(List.of());
            assertThat(chatController.getConversation(1L, 2L, 0, 50).getBody()).isEmpty();
        }

        @Test @DisplayName("getUserConversations")
        void getUserConversations() {
            when(chatService.getUserConversations(1L)).thenReturn(List.of());
            assertThat(chatController.getUserConversations(1L).getBody()).isEmpty();
        }

        @Test @DisplayName("markAsRead")
        void markAsRead() {
            ResponseEntity<Void> resp = chatController.markAsRead(1L, 2L);
            verify(chatService).markAsRead(1L, 2L);
            assertThat(resp.getStatusCode().value()).isEqualTo(200);
        }

        @Test @DisplayName("getTotalUnreadCount")
        void getTotalUnreadCount() {
            when(chatService.getTotalUnreadCount(1L)).thenReturn(5);
            assertThat(chatController.getTotalUnreadCount(1L).getBody()).isEqualTo(5);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // DOCUMENT CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class DocumentControllerTests {
        @Mock private DocumentService documentService;
        @InjectMocks private DocumentController documentController;

        @Test @DisplayName("downloadFile")
        void downloadFile() {
            Document doc = Document.builder().id(1L).fileName("test.pdf").contentType("application/pdf").build();
            when(documentService.getDocumentById(1L)).thenReturn(doc);
            when(documentService.downloadDocument(1L)).thenReturn(new byte[]{1, 2, 3});

            ResponseEntity<byte[]> resp = documentController.downloadFile(1L);
            assertThat(resp.getStatusCode().value()).isEqualTo(200);
            assertThat(resp.getBody()).hasSize(3);
        }

        @Test @DisplayName("downloadFile — contentType null usa application/octet-stream")
        void downloadFile_nullContentType() {
            Document doc = Document.builder().id(1L).fileName("file.bin").contentType(null).build();
            when(documentService.getDocumentById(1L)).thenReturn(doc);
            when(documentService.downloadDocument(1L)).thenReturn(new byte[]{});

            ResponseEntity<byte[]> resp = documentController.downloadFile(1L);
            assertThat(resp.getHeaders().getContentType().toString()).isEqualTo("application/octet-stream");
        }

        @Test @DisplayName("getUserDocuments")
        void getUserDocuments() {
            when(documentService.getUserDocumentsDto(1L)).thenReturn(List.of());
            assertThat(documentController.getUserDocuments(1L).getBody()).isEmpty();
        }

        @Test @DisplayName("getUserDocumentsByType")
        void getUserDocumentsByType() {
            when(documentService.getUserDocumentsByTypeDto(1L, "WORKOUT_PLAN")).thenReturn(List.of());
            assertThat(documentController.getUserDocumentsByType(1L, "WORKOUT_PLAN").getBody()).isEmpty();
        }

        @Test @DisplayName("deleteDocument")
        void deleteDocument() {
            ResponseEntity<Void> resp = documentController.deleteDocument(1L);
            verify(documentService).deleteDocument(1L);
            assertThat(resp.getStatusCode().value()).isEqualTo(204);
        }

        @Test @DisplayName("updateNotes")
        void updateNotes() {
            when(documentService.updateNotes(1L, "nuove note")).thenReturn(Map.of("notes", "nuove note"));
            ResponseEntity<Map<String, Object>> resp = documentController.updateNotes(1L, Map.of("notes", "nuove note"));
            assertThat(resp.getBody().get("notes")).isEqualTo("nuove note");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // JOB APPLICATION CONTROLLER
    // ═══════════════════════════════════════════════════════════
    @Nested
    class JobApplicationControllerTests {
        @Mock private EmailService emailService;
        @InjectMocks private JobApplicationController jobApplicationController;

        @Test @DisplayName("submitApplication")
        void submitApplication() {
            JobApplicationRequest req = new JobApplicationRequest();
            ResponseEntity<Map<String, String>> resp = jobApplicationController.submitApplication(req, null);
            verify(emailService).sendJobApplication(req, null);
            assertThat(resp.getBody().get("message")).contains("Candidatura");
        }
    }
}

