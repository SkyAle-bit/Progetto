package com.project.tesi.service.impl;

import com.project.tesi.enums.PlanDuration;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.*;
import com.project.tesi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PlanRepository planRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AdminServiceImpl adminService;

    private User user;
    private Plan plan;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).firstName("Mario").lastName("Rossi")
                .email("mario@test.com").role(Role.CLIENT).build();
        plan = Plan.builder().id(1L).name("Premium").duration(PlanDuration.ANNUALE)
                .monthlyCreditsPT(8).monthlyCreditsNutri(4)
                .fullPrice(1200.0).monthlyInstallmentPrice(100.0).build();
    }

    @Test @DisplayName("getAllUsers — restituisce lista utenti")
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<Map<String, Object>> result = adminService.getAllUsers();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("email")).isEqualTo("mario@test.com");
    }

    @Test @DisplayName("createUser — crea PT senza professionisti assegnati")
    void createUser_pt() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "pt@test.com"); body.put("firstName", "Luca");
        body.put("lastName", "Bianchi"); body.put("password", "pass"); body.put("role", "PERSONAL_TRAINER");

        when(userRepository.findByEmail("pt@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User saved = User.builder().id(2L).firstName("Luca").lastName("Bianchi")
                .email("pt@test.com").role(Role.PERSONAL_TRAINER).build();
        when(userRepository.save(any())).thenReturn(saved);

        Map<String, Object> result = adminService.createUser(body);
        assertThat(result.get("role")).isEqualTo("PERSONAL_TRAINER");
    }

    @Test @DisplayName("createUser — CLIENT con piano e professionisti")
    void createUser_clientWithPlan() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "new@test.com"); body.put("firstName", "Nuovo");
        body.put("lastName", "Utente"); body.put("password", "pass"); body.put("role", "CLIENT");
        body.put("assignedPTId", 2); body.put("assignedNutritionistId", 3); body.put("planId", 1);

        User pt = User.builder().id(2L).role(Role.PERSONAL_TRAINER).build();
        User nutri = User.builder().id(3L).role(Role.NUTRITIONIST).build();

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User saved = User.builder().id(10L).firstName("Nuovo").lastName("Utente")
                .email("new@test.com").role(Role.CLIENT).build();
        when(userRepository.save(any())).thenReturn(saved);
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        Map<String, Object> result = adminService.createUser(body);
        assertThat(result.get("role")).isEqualTo("CLIENT");
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test @DisplayName("createUser — campi mancanti lancia IllegalArgumentException")
    void createUser_missingFields() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "test@test.com");
        assertThatThrownBy(() -> adminService.createUser(body)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("createUser — email duplicata lancia ResourceAlreadyExistsException")
    void createUser_duplicate() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "mario@test.com"); body.put("firstName", "X");
        body.put("lastName", "Y"); body.put("password", "p"); body.put("role", "CLIENT");
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> adminService.createUser(body)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test @DisplayName("deleteUser — elimina utente con documenti e abbonamento")
    void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findByOwner(user)).thenReturn(List.of());
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.empty());
        adminService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test @DisplayName("deleteUser — utente non trovato")
    void deleteUser_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminService.deleteUser(999L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("getAllSubscriptions — restituisce lista")
    void getAllSubscriptions() {
        Subscription sub = Subscription.builder().id(1L).user(user).plan(plan)
                .active(true).startDate(java.time.LocalDate.now()).endDate(java.time.LocalDate.now().plusYears(1)).build();
        when(subscriptionRepository.findAll()).thenReturn(List.of(sub));
        List<Map<String, Object>> result = adminService.getAllSubscriptions();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("planName")).isEqualTo("Premium");
    }

    @Test @DisplayName("createPlan — successo")
    void createPlan_success() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Gold"); body.put("duration", "ANNUALE");
        body.put("fullPrice", 1000.0); body.put("monthlyInstallmentPrice", 90.0);
        body.put("monthlyCreditsPT", 6); body.put("monthlyCreditsNutri", 3);

        when(planRepository.findByName("Gold")).thenReturn(Optional.empty());
        Plan saved = Plan.builder().id(2L).name("Gold").duration(PlanDuration.ANNUALE)
                .fullPrice(1000.0).monthlyInstallmentPrice(90.0).build();
        when(planRepository.save(any())).thenReturn(saved);

        Map<String, Object> result = adminService.createPlan(body);
        assertThat(result.get("name")).isEqualTo("Gold");
    }

    @Test @DisplayName("createPlan — campi mancanti")
    void createPlan_missingFields() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Gold");
        assertThatThrownBy(() -> adminService.createPlan(body)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("createPlan — nome duplicato")
    void createPlan_duplicate() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Premium"); body.put("duration", "ANNUALE");
        body.put("fullPrice", 1000.0); body.put("monthlyInstallmentPrice", 90.0);
        when(planRepository.findByName("Premium")).thenReturn(Optional.of(plan));
        assertThatThrownBy(() -> adminService.createPlan(body)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test @DisplayName("deletePlan — successo")
    void deletePlan_success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findAll()).thenReturn(List.of());
        adminService.deletePlan(1L);
        verify(planRepository).delete(plan);
    }

    @Test @DisplayName("deletePlan — piano non trovato")
    void deletePlan_notFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminService.deletePlan(999L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("deletePlan — piano con sottoscrittori attivi lancia IllegalStateException")
    void deletePlan_hasSubscribers() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        Subscription sub = Subscription.builder().id(1L).plan(plan).build();
        when(subscriptionRepository.findAll()).thenReturn(List.of(sub));
        assertThatThrownBy(() -> adminService.deletePlan(1L)).isInstanceOf(IllegalStateException.class);
    }

    @Test @DisplayName("createPlan — senza crediti opzionali (null)")
    void createPlan_nullCredits() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Basic"); body.put("duration", "SEMESTRALE");
        body.put("fullPrice", 500.0); body.put("monthlyInstallmentPrice", 90.0);
        // monthlyCreditsPT e monthlyCreditsNutri non specificati

        when(planRepository.findByName("Basic")).thenReturn(Optional.empty());
        Plan saved = Plan.builder().id(3L).name("Basic").duration(PlanDuration.SEMESTRALE)
                .fullPrice(500.0).monthlyInstallmentPrice(90.0).build();
        when(planRepository.save(any())).thenReturn(saved);

        Map<String, Object> result = adminService.createPlan(body);
        assertThat(result.get("name")).isEqualTo("Basic");
    }

    // ══════════════ BRANCH AGGIUNTIVE ══════════════

    @Test @DisplayName("getAllSubscriptions — plan null mostra N/A e prezzo 0")
    void getAllSubscriptions_planNull() {
        Subscription sub = Subscription.builder().id(1L).user(user).plan(null)
                .active(false).startDate(null).endDate(null).build();
        when(subscriptionRepository.findAll()).thenReturn(List.of(sub));
        List<Map<String, Object>> result = adminService.getAllSubscriptions();
        assertThat(result.get(0).get("planName")).isEqualTo("N/A");
        assertThat(((Number) result.get(0).get("monthlyPrice")).intValue()).isEqualTo(0);
        assertThat(result.get(0).get("startDate")).isNull();
        assertThat(result.get(0).get("endDate")).isNull();
    }

    @Test @DisplayName("deleteUser — con abbonamento presente lo elimina")
    void deleteUser_withSubscription() {
        Subscription sub = Subscription.builder().id(1L).user(user).plan(plan).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findByOwner(user)).thenReturn(List.of());
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(sub));

        adminService.deleteUser(1L);
        verify(subscriptionRepository).delete(sub);
        verify(userRepository).delete(user);
    }

    @Test @DisplayName("createUser — CLIENT senza professionisti assegnati (ids null)")
    void createUser_clientNoProfessionals() {
        Map<String, Object> body = new HashMap<>();
        body.put("email", "new@test.com"); body.put("firstName", "New");
        body.put("lastName", "User"); body.put("password", "pass"); body.put("role", "CLIENT");
        // niente assignedPTId, assignedNutritionistId, planId

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User saved = User.builder().id(10L).firstName("New").lastName("User")
                .email("new@test.com").role(Role.CLIENT).build();
        when(userRepository.save(any())).thenReturn(saved);

        Map<String, Object> result = adminService.createUser(body);
        assertThat(result.get("role")).isEqualTo("CLIENT");
        // Nessun abbonamento creato
        verify(subscriptionRepository, never()).save(any());
    }

    @Test @DisplayName("createUser — CLIENT con piano SEMESTRALE")
    void createUser_clientSemestrale() {
        Plan semPlan = Plan.builder().id(2L).name("Semi").duration(PlanDuration.SEMESTRALE)
                .monthlyCreditsPT(4).monthlyCreditsNutri(2).build();
        Map<String, Object> body = new HashMap<>();
        body.put("email", "semi@test.com"); body.put("firstName", "Semi");
        body.put("lastName", "User"); body.put("password", "pass"); body.put("role", "CLIENT");
        body.put("planId", 2);

        when(userRepository.findByEmail("semi@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User saved = User.builder().id(11L).firstName("Semi").lastName("User")
                .email("semi@test.com").role(Role.CLIENT).build();
        when(userRepository.save(any())).thenReturn(saved);
        when(planRepository.findById(2L)).thenReturn(Optional.of(semPlan));

        adminService.createUser(body);
        verify(subscriptionRepository).save(any(Subscription.class));
    }
}



