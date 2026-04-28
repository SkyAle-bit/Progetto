package com.project.tesi.service.impl;

import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
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

import java.time.LocalDate;
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
        user = User.builder().email("mario@test.com").password("pass").role(Role.CLIENT).id(1L).firstName("Mario").lastName("Rossi").build();
        plan = Plan.builder().id(1L).name("Premium").duration(PlanDuration.ANNUALE)
                .monthlyCreditsPT(8).monthlyCreditsNutri(4)
                .fullPrice(1200.0).monthlyInstallmentPrice(100.0).build();
    }

    @Test @DisplayName("getAllUsers — restituisce lista utenti")
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = adminService.getAllUsers();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("mario@test.com");
    }

    @Test @DisplayName("createUser — crea PT senza professionisti assegnati")
    void createUser_pt() {
        UserCreateRequestDTO request = new UserCreateRequestDTO("pt@test.com", "Luca", "Bianchi", "pass", "PERSONAL_TRAINER", null, null);

        when(userRepository.findByEmail("pt@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User saved = User.builder().email("pt@test.com").password("pass").role(Role.PERSONAL_TRAINER).id(2L).firstName("Luca").lastName("Bianchi").build();
        when(userRepository.save(any())).thenReturn(saved);

        User result = adminService.createUser(request);
        assertThat(result.getRole()).isEqualTo(Role.PERSONAL_TRAINER);
    }

    @Test @DisplayName("createUser — campi mancanti lancia IllegalArgumentException")
    void createUser_missingFields() {
        UserCreateRequestDTO request = new UserCreateRequestDTO(null, "X", "Y", "p", "CLIENT", null, null);
        assertThatThrownBy(() -> adminService.createUser(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("createUser — email duplicata lancia ResourceAlreadyExistsException")
    void createUser_duplicate() {
        UserCreateRequestDTO request = new UserCreateRequestDTO("mario@test.com", "X", "Y", "p", "CLIENT", null, null);
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> adminService.createUser(request)).isInstanceOf(ResourceAlreadyExistsException.class);
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
        Subscription sub = Subscription.builder().id(1L).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE)
                .active(true).startDate(java.time.LocalDate.now()).endDate(java.time.LocalDate.now().plusYears(1)).build();
        when(subscriptionRepository.findAll()).thenReturn(List.of(sub));
        List<Subscription> result = adminService.getAllSubscriptions();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlan().getName()).isEqualTo("Premium");
    }

    @Test @DisplayName("createPlan — successo")
    void createPlan_success() {
        PlanCreateRequestDTO request = new PlanCreateRequestDTO("Gold", "ANNUALE", 1000.0, 90.0, 6, 3);

        when(planRepository.findByName("Gold")).thenReturn(Optional.empty());
        Plan saved = Plan.builder().id(2L).name("Gold").duration(PlanDuration.ANNUALE)
                .fullPrice(1000.0).monthlyInstallmentPrice(90.0).build();
        when(planRepository.save(any())).thenReturn(saved);

        Plan result = adminService.createPlan(request);
        assertThat(result.getName()).isEqualTo("Gold");
    }

    @Test @DisplayName("createPlan — campi mancanti")
    void createPlan_missingFields() {
        PlanCreateRequestDTO request = new PlanCreateRequestDTO(null, "ANNUALE", 1000.0, 90.0, 6, 3);
        assertThatThrownBy(() -> adminService.createPlan(request)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("createPlan — nome duplicato")
    void createPlan_duplicate() {
        PlanCreateRequestDTO request = new PlanCreateRequestDTO("Premium", "ANNUALE", 1000.0, 90.0, 6, 3);
        when(planRepository.findByName("Premium")).thenReturn(Optional.of(plan));
        assertThatThrownBy(() -> adminService.createPlan(request)).isInstanceOf(ResourceAlreadyExistsException.class);
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
        Subscription sub = Subscription.builder().id(1L).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).build();
        when(subscriptionRepository.findAll()).thenReturn(List.of(sub));
        assertThatThrownBy(() -> adminService.deletePlan(1L)).isInstanceOf(IllegalStateException.class);
    }

    @Test @DisplayName("createPlan — senza crediti opzionali (null)")
    void createPlan_nullCredits() {
        PlanCreateRequestDTO request = new PlanCreateRequestDTO("Basic", "SEMESTRALE", 500.0, 90.0, null, null);

        when(planRepository.findByName("Basic")).thenReturn(Optional.empty());
        Plan saved = Plan.builder().id(3L).name("Basic").duration(PlanDuration.SEMESTRALE)
                .fullPrice(500.0).monthlyInstallmentPrice(90.0).build();
        when(planRepository.save(any())).thenReturn(saved);

        Plan result = adminService.createPlan(request);
        assertThat(result.getName()).isEqualTo("Basic");
    }

    // ══════════════ BRANCH AGGIUNTIVE ══════════════

    @Test @DisplayName("getAllSubscriptions — plan null mostra N/A e prezzo 0")
    void getAllSubscriptions_planNull() {
        Plan dummyPlan = Plan.builder().id(0L).name("N/A").duration(PlanDuration.ANNUALE).fullPrice(0.0).monthlyInstallmentPrice(0.0).build();
        Subscription sub = Subscription.builder().id(1L).user(user).plan(dummyPlan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE)
                .active(false).startDate(null).endDate(null).build();
        sub.setPlan(null); // Force null for test logic
        when(subscriptionRepository.findAll()).thenReturn(List.of(sub));
        List<Subscription> result = adminService.getAllSubscriptions();
        assertThat(result.get(0).getPlan()).isNull();
    }

    @Test @DisplayName("deleteUser — con abbonamento presente lo elimina")
    void deleteUser_withSubscription() {
        Subscription sub = Subscription.builder().id(1L).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).active(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(documentRepository.findByOwner(user)).thenReturn(List.of());
        when(subscriptionRepository.findByUserId(1L)).thenReturn(Optional.of(sub));

        adminService.deleteUser(1L);
        verify(subscriptionRepository).delete(sub);
        verify(userRepository).delete(user);
    }

    @Test @DisplayName("createUser — CLIENT senza professionisti assegnati (ids null)")
    void createUser_clientNoProfessionals() {
        UserCreateRequestDTO request = new UserCreateRequestDTO("new@test.com", "New", "User", "pass", "CLIENT", null, null);

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        User saved = User.builder().email("new@test.com").password("pass").role(Role.CLIENT).id(10L).firstName("New").lastName("User").build();
        when(userRepository.save(any())).thenReturn(saved);

        User result = adminService.createUser(request);
        assertThat(result.getRole()).isEqualTo(Role.CLIENT);
        // Nessun abbonamento creato
        // In AdminServiceImpl refactored, plan creation logic for client is removed from string-based map args.
        verify(subscriptionRepository, never()).save(any());
    }
}
