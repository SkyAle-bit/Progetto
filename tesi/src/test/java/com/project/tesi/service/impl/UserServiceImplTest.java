package com.project.tesi.service.impl;

import com.project.tesi.dto.request.ProfileUpdateRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.*;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.ProfessionalSoldOutException;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.mapper.BookingMapper;
import com.project.tesi.mapper.SubscriptionMapper;
import com.project.tesi.mapper.UserMapper;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private PlanRepository planRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private UserMapper userMapper;
    @Mock private SubscriptionMapper subscriptionMapper;
    @Mock private BookingMapper bookingMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserServiceImpl userService;

    private User client, pt, nutri, admin;
    private Plan plan;

    @BeforeEach
    void setUp() {
        pt = User.builder().id(2L).firstName("Luca").lastName("Bianchi").role(Role.PERSONAL_TRAINER).build();
        nutri = User.builder().id(3L).firstName("Sara").lastName("Verdi").role(Role.NUTRITIONIST).build();
        client = User.builder().id(1L).firstName("Mario").lastName("Rossi").role(Role.CLIENT)
                .email("mario@test.com").assignedPT(pt).assignedNutritionist(nutri)
                .createdAt(LocalDateTime.now().minusMonths(2)).build();
        admin = User.builder().id(99L).firstName("Admin").lastName("Admin").role(Role.ADMIN).email("admin@test.com").build();
        plan = Plan.builder().id(1L).name("Premium").duration(PlanDuration.ANNUALE)
                .monthlyCreditsPT(8).monthlyCreditsNutri(4).build();
    }

    @Test @DisplayName("updateProfile — aggiorna tutti i campi")
    void updateProfile_allFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedPass");

        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setFirstName("Marco"); req.setLastName("Bianchi"); req.setPassword("newPass");

        userService.updateProfile(1L, req);

        assertThat(client.getFirstName()).isEqualTo("Marco");
        assertThat(client.getLastName()).isEqualTo("Bianchi");
        assertThat(client.getPassword()).isEqualTo("hashedPass");
        verify(userRepository).save(client);
    }

    @Test @DisplayName("updateProfile — campi null non aggiornano")
    void updateProfile_nullFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        ProfileUpdateRequest req = new ProfileUpdateRequest();
        userService.updateProfile(1L, req);
        assertThat(client.getFirstName()).isEqualTo("Mario"); // non modificato
    }

    @Test @DisplayName("updateProfile — utente non trovato")
    void updateProfile_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateProfile(999L, new ProfileUpdateRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("registerUser — registrazione riuscita")
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setFirstName("Nuovo"); req.setLastName("Utente");
        req.setSelectedPtId(2L); req.setSelectedNutritionistId(3L);
        req.setSelectedPlanId(1L); req.setPaymentFrequency(PaymentFrequency.UNICA_SOLUZIONE);

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        User newUser = User.builder().email("new@test.com").role(Role.CLIENT).build();
        when(userMapper.toUser(req)).thenReturn(newUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.countByAssignedPT(pt)).thenReturn(5L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(userRepository.countByAssignedNutritionist(nutri)).thenReturn(3L);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionMapper.toSubscription(eq(req), eq(newUser), eq(plan)))
                .thenReturn(Subscription.builder().build());
        when(userMapper.toUserResponse(newUser)).thenReturn(UserResponse.builder().email("new@test.com").build());

        UserResponse result = userService.registerUser(req);
        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test @DisplayName("registerUser — email già esistente")
    void registerUser_emailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("mario@test.com");
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(client));
        assertThatThrownBy(() -> userService.registerUser(req)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test @DisplayName("registerUser — PT soldout")
    void registerUser_ptSoldOut() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setSelectedPtId(2L); req.setSelectedNutritionistId(3L);
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userMapper.toUser(req)).thenReturn(User.builder().role(Role.CLIENT).build());
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.countByAssignedPT(pt)).thenReturn(10L); // SOLD OUT!

        assertThatThrownBy(() -> userService.registerUser(req)).isInstanceOf(ProfessionalSoldOutException.class);
    }

    @Test @DisplayName("registerUser — ptId null lancia IllegalArgument")
    void registerUser_ptIdNull() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setSelectedPtId(null); req.setSelectedNutritionistId(3L);
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userMapper.toUser(req)).thenReturn(User.builder().role(Role.CLIENT).build());

        assertThatThrownBy(() -> userService.registerUser(req)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("findAvailableProfessionals — restituisce lista ordinata per rating")
    void findAvailableProfessionals() {
        when(userRepository.findByRole(Role.PERSONAL_TRAINER)).thenReturn(List.of(pt));
        when(reviewRepository.getAverageRating(2L)).thenReturn(4.5);
        when(userRepository.countByAssignedPT(pt)).thenReturn(5L);

        List<ProfessionalSummaryDTO> result = userService.findAvailableProfessionals(Role.PERSONAL_TRAINER);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageRating()).isEqualTo(4.5);
        assertThat(result.get(0).isSoldOut()).isFalse();
    }

    @Test @DisplayName("findAvailableProfessionals — rating null → default 0.0")
    void findAvailableProfessionals_nullRating() {
        when(userRepository.findByRole(Role.NUTRITIONIST)).thenReturn(List.of(nutri));
        when(reviewRepository.getAverageRating(3L)).thenReturn(null);
        when(userRepository.countByAssignedNutritionist(nutri)).thenReturn(10L);

        List<ProfessionalSummaryDTO> result = userService.findAvailableProfessionals(Role.NUTRITIONIST);
        assertThat(result.get(0).getAverageRating()).isEqualTo(0.0);
        assertThat(result.get(0).isSoldOut()).isTrue();
    }

    @Test @DisplayName("getClientDashboard — cliente con abbonamento e prenotazioni")
    void getClientDashboard_client() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        Subscription sub = Subscription.builder().id(1L).plan(plan).active(true)
                .startDate(java.time.LocalDate.now()).endDate(java.time.LocalDate.now().plusYears(1))
                .currentCreditsPT(8).currentCreditsNutri(4).build();
        when(subscriptionRepository.findByUserAndActiveTrue(client)).thenReturn(Optional.of(sub));
        when(bookingRepository.findFutureByUser(eq(client), any())).thenReturn(List.of());
        when(userMapper.toUserResponse(client)).thenReturn(UserResponse.builder().id(1L).build());

        ClientDashboardResponse result = userService.getClientDashboard(1L);
        assertThat(result).isNotNull();
        assertThat(result.getFollowingProfessionals()).isNotEmpty();
        assertThat(result.getSubscription()).isNotNull();
    }

    @Test @DisplayName("getClientDashboard — professionista restituisce dashboard semplificata")
    void getClientDashboard_professional() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(bookingRepository.findByProfessional(pt)).thenReturn(List.of());
        when(userMapper.toUserResponse(pt)).thenReturn(UserResponse.builder().id(2L).build());

        ClientDashboardResponse result = userService.getClientDashboard(2L);
        assertThat(result.getSubscription()).isNull();
        assertThat(result.getFollowingProfessionals()).isEmpty();
    }

    @Test @DisplayName("getClientsForProfessional — PT con clienti")
    void getClientsForProfessional_pt() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.findByAssignedPT(pt)).thenReturn(List.of(client));

        List<ClientBasicInfoResponse> result = userService.getClientsForProfessional(2L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Mario");
    }

    @Test @DisplayName("getClientsForProfessional — nutrizionista")
    void getClientsForProfessional_nutri() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(userRepository.findByAssignedNutritionist(nutri)).thenReturn(List.of(client));

        List<ClientBasicInfoResponse> result = userService.getClientsForProfessional(3L);
        assertThat(result).hasSize(1);
    }

    @Test @DisplayName("getClientsForProfessional — non professionista lancia IllegalArgument")
    void getClientsForProfessional_notProfessional() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        assertThatThrownBy(() -> userService.getClientsForProfessional(1L)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("getAdmin — restituisce dati admin")
    void getAdmin_success() {
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin));
        ClientBasicInfoResponse result = userService.getAdmin();
        assertThat(result.getId()).isEqualTo(99L);
    }

    @Test @DisplayName("getAdmin — admin non trovato")
    void getAdmin_notFound() {
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of());
        assertThatThrownBy(() -> userService.getAdmin()).isInstanceOf(ResourceNotFoundException.class);
    }

    // ══════════════ BRANCH AGGIUNTIVE ══════════════

    @Test @DisplayName("updateProfile — profilePicture valida viene aggiornata")
    void updateProfile_profilePicture() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setProfilePicture("https://example.com/photo.jpg");
        userService.updateProfile(1L, req);
        assertThat(client.getProfilePicture()).isEqualTo("https://example.com/photo.jpg");
    }

    @Test @DisplayName("updateProfile — campi blank (spazi) non aggiornano")
    void updateProfile_blankFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setFirstName("   "); req.setLastName("   "); req.setPassword("   "); req.setProfilePicture("   ");
        userService.updateProfile(1L, req);
        assertThat(client.getFirstName()).isEqualTo("Mario"); // non cambiato
    }

    @Test @DisplayName("registerUser — senza piano non crea abbonamento")
    void registerUser_withoutPlan() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setSelectedPtId(2L); req.setSelectedNutritionistId(3L);
        req.setSelectedPlanId(null); req.setPaymentFrequency(null); // senza piano

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        User newUser = User.builder().email("new@test.com").role(Role.CLIENT).build();
        when(userMapper.toUser(req)).thenReturn(newUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.countByAssignedPT(pt)).thenReturn(5L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(userRepository.countByAssignedNutritionist(nutri)).thenReturn(3L);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(userMapper.toUserResponse(newUser)).thenReturn(UserResponse.builder().email("new@test.com").build());

        userService.registerUser(req);
        verify(subscriptionRepository, never()).save(any());
    }

    @Test @DisplayName("registerUser — professionista con ruolo sbagliato lancia IllegalArgument")
    void registerUser_wrongProfessionalRole() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setSelectedPtId(3L); req.setSelectedNutritionistId(2L);
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userMapper.toUser(req)).thenReturn(User.builder().role(Role.CLIENT).build());
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri)); // nutri al posto di PT!

        assertThatThrownBy(() -> userService.registerUser(req)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("registerUser — nutrizionista sold out")
    void registerUser_nutriSoldOut() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("new@test.com"); req.setSelectedPtId(2L); req.setSelectedNutritionistId(3L);
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userMapper.toUser(req)).thenReturn(User.builder().role(Role.CLIENT).build());
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.countByAssignedPT(pt)).thenReturn(5L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(userRepository.countByAssignedNutritionist(nutri)).thenReturn(10L); // SOLD OUT

        assertThatThrownBy(() -> userService.registerUser(req)).isInstanceOf(ProfessionalSoldOutException.class);
    }

    @Test @DisplayName("getClientDashboard — cliente senza abbonamento attivo")
    void getClientDashboard_noSubscription() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(subscriptionRepository.findByUserAndActiveTrue(client)).thenReturn(Optional.empty());
        when(bookingRepository.findFutureByUser(eq(client), any())).thenReturn(List.of());
        when(userMapper.toUserResponse(client)).thenReturn(UserResponse.builder().id(1L).build());

        ClientDashboardResponse result = userService.getClientDashboard(1L);
        assertThat(result.getSubscription()).isNull();
    }

    @Test @DisplayName("getClientDashboard — cliente senza PT assegnato")
    void getClientDashboard_noPT() {
        User clientNoPT = User.builder().id(10L).firstName("Anna").lastName("Neri")
                .role(Role.CLIENT).assignedPT(null).assignedNutritionist(nutri)
                .createdAt(LocalDateTime.now()).build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(clientNoPT));
        when(subscriptionRepository.findByUserAndActiveTrue(clientNoPT)).thenReturn(Optional.empty());
        when(bookingRepository.findFutureByUser(eq(clientNoPT), any())).thenReturn(List.of());
        when(userMapper.toUserResponse(clientNoPT)).thenReturn(UserResponse.builder().id(10L).build());

        ClientDashboardResponse result = userService.getClientDashboard(10L);
        assertThat(result.getFollowingProfessionals()).hasSize(1); // solo nutri
    }

    @Test @DisplayName("getClientDashboard — cliente senza professionisti assegnati")
    void getClientDashboard_noProfessionals() {
        User clientNone = User.builder().id(11L).firstName("Bob").lastName("Test")
                .role(Role.CLIENT).assignedPT(null).assignedNutritionist(null)
                .createdAt(LocalDateTime.now()).build();
        when(userRepository.findById(11L)).thenReturn(Optional.of(clientNone));
        when(subscriptionRepository.findByUserAndActiveTrue(clientNone)).thenReturn(Optional.empty());
        when(bookingRepository.findFutureByUser(eq(clientNone), any())).thenReturn(List.of());
        when(userMapper.toUserResponse(clientNone)).thenReturn(UserResponse.builder().id(11L).build());

        ClientDashboardResponse result = userService.getClientDashboard(11L);
        assertThat(result.getFollowingProfessionals()).isEmpty();
    }

    @Test @DisplayName("getClientsForProfessional — client con profilePicture non null")
    void getClientsForProfessional_withProfilePicture() {
        User clientWithPic = User.builder().id(5L).firstName("Anna").lastName("Neri")
                .email("anna@test.com").role(Role.CLIENT).profilePicture("pic.jpg").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.findByAssignedPT(pt)).thenReturn(List.of(clientWithPic));

        List<ClientBasicInfoResponse> result = userService.getClientsForProfessional(2L);
        assertThat(result.get(0).getProfilePictureUrl()).isEqualTo("pic.jpg");
    }

    @Test @DisplayName("getAdmin — admin con profilePicture usa profilePicture")
    void getAdmin_withProfilePicture() {
        admin.setProfilePicture("admin-pic.jpg");
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin));
        ClientBasicInfoResponse result = userService.getAdmin();
        assertThat(result.getProfilePictureUrl()).isEqualTo("admin-pic.jpg");
    }

    @Test @DisplayName("getClientDashboard — nutrizionista restituisce dashboard semplificata")
    void getClientDashboard_nutritionist() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(bookingRepository.findByProfessional(nutri)).thenReturn(List.of());
        when(userMapper.toUserResponse(nutri)).thenReturn(UserResponse.builder().id(3L).build());

        ClientDashboardResponse result = userService.getClientDashboard(3L);
        assertThat(result.getSubscription()).isNull();
        assertThat(result.getFollowingProfessionals()).isEmpty();
    }
}


