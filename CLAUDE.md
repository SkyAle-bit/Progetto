# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

The project lives under `tesi/`. All Maven commands run from that directory.

```bash
cd tesi

# Run with local Docker PostgreSQL (dev profile — starts Docker Compose automatically)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with production database (default profile)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=BookingServiceImplTest

# Build JAR (skip tests)
./mvnw clean package -DskipTests
```

**Prerequisites for dev profile:** Java 21, Docker Desktop running.

**Test credentials** (from DataInitializer, dev only):
- Admin: `admin@example.com` / `admin`
- Client: `client@example.com` / `client`
- Personal Trainer: `pt@example.com` / `pt`

## Architecture

Single Spring Boot 4 monolith with a strict layered flow:

```
Controllers → Facades → Services → Builders → Repositories → PostgreSQL
```

- **Controllers** (`controller/`) — REST endpoints; delegate entirely to facades or services, no business logic.
- **Facades** (`facade/`) — Orchestrate multiple services for complex operations (e.g., `BookingFacade` calls `BookingService`, `SubscriptionService`, `SlotService`, and fires events).
- **Services** (`service/impl/`) — Business logic. Interfaces under `service/`, implementations under `service/impl/`.
- **Builders** (`builder/impl/`) — Entity construction via the Builder pattern; all entities are assembled through builders.
- **Repositories** (`repository/`) — Spring Data JPA; no custom SQL except JPQL in `@Query` annotations.

### Key Design Patterns

- **Strategy** — `BookingStrategy` interface with `PTBookingStrategy` and `NutritionistBookingStrategy`; the facade selects the right strategy at runtime.
- **Observer** — `EventManager` dispatches booking/cancellation events to `ActivityFeedUpdateListener` and email notification listeners.
- **Optimistic Locking + Fine-Grained Locking** — `Slot` carries `@Version`; a `ConcurrentHashMap<Long, ReentrantLock>` in `BookingServiceImpl` prevents duplicate booking under concurrent requests.

### Domain Overview

| Concept | Key rules |
|---|---|
| **Subscription** | Plans are Basic (1+1 credits/month) or Premium (2+2 credits/month); semi-annual or annual, lump-sum or installments |
| **Booking** | Deducts credits; uses slot locking to prevent overbooking |
| **Slot** | 30-minute windows generated from a `WeeklySchedule`; max 50 clients per professional |
| **Review** | One review per client–professional pair; only clients who have booked can review |
| **Chat** | Real-time via STOMP/WebSocket; REST fallback for history |
| **Document** | Files stored on filesystem with metadata in DB; separate types per role |

### Roles

`CLIENT`, `PERSONAL_TRAINER`, `NUTRITIONIST`, `MODERATOR`, `INSURANCE_MANAGER`, `ADMIN`

### Background Jobs

- `SubscriptionScheduler` — runs daily at midnight; resets monthly credits and processes installment charges.
- `BookingReminderScheduler` — sends reminder emails before upcoming appointments.

Both are disabled automatically during tests via Spring test profile configuration.

## Profiles & Configuration

| Profile | DB | Docker Compose |
|---|---|---|
| `dev` | Local PostgreSQL (`localhost:5432`) | Auto-started |
| `prod` (default) | Supabase via Transaction Pooler | Disabled |

Secrets come from environment variables: `JWT_SECRET`, `MAIL_FROM`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`.

## Testing

Tests use JUnit 5 + Mockito + Spring Test. The test profile uses H2 in-memory with `create-drop` DDL.

Pattern used throughout:
- `@ExtendWith(MockitoExtension.class)` + `@Mock`/`@InjectMocks` for pure unit tests
- `MockMvc` + `@WebMvcTest` for controller-layer tests
- `@DisplayName` on every test method for readable output

Tests mirror the source tree under `src/test/java/com/project/tesi/`.

## API Documentation

Swagger UI is available at `/swagger-ui.html` when the app is running (via springdoc-openapi).
