# FitConnect — Piattaforma SaaS per il Wellness Integrato

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql)
![Tests](https://img.shields.io/badge/tests-260_passing-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

Backend RESTful per una piattaforma SaaS che connette clienti con Personal Trainer, Nutrizionisti e partner assicurativi in un unico abbonamento. Costruito come monolite modulare su Java 21 e Spring Boot 4 con un'architettura rigorosa a layer.

---

## Indice

1. [Tech Stack](#tech-stack)
2. [Architettura](#architettura)
3. [Domain Model](#domain-model)
4. [Quick Start](#quick-start)
5. [Credenziali di Test](#credenziali-di-test)
6. [API Docs](#api-docs)
7. [Configurazione](#configurazione)
8. [Testing](#testing)

---

## Tech Stack

| Categoria | Tecnologia |
|---|---|
| Runtime | Java 21 (LTS) |
| Framework | Spring Boot 4 (Web, Data JPA, Security, WebSocket, Validation) |
| Database | PostgreSQL 16 |
| Sicurezza | Spring Security + JWT (stateless) |
| Messaggistica real-time | STOMP / WebSocket |
| Logging | Log4j2 (via SLF4J) |
| Documentazione API | SpringDoc OpenAPI 3 / Swagger UI |
| Build | Maven Wrapper (`mvnw`) |
| Container | Docker Compose (PostgreSQL + pgAdmin) |
| Testing | JUnit 5, Mockito, H2 (in-memory) |

---

## Architettura

Il flusso di ogni richiesta segue un percorso unidirezionale senza salti di layer:

```
HTTP Request
     │
     ▼
┌──────────────┐
│  Controller  │  Validazione input, estrazione principal JWT
└──────┬───────┘
       │
       ▼
┌──────────────┐
│    Facade    │  Orchestrazione multi-servizio, transazioni di alto livello
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Service    │  Business logic, locking, eventi Spring, strategia di crediti
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Builder    │  Costruzione entità (Builder Pattern)
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Repository  │  Spring Data JPA → PostgreSQL
└──────────────┘
```

### Pattern di Design

| Pattern | Dove |
|---|---|
| **Strategy** | `BookingStrategy`: `PTBookingStrategy` e `NutritionistBookingStrategy` selezionati a runtime per scalare crediti |
| **Observer (Spring Events)** | `ApplicationEventPublisher` + `@TransactionalEventListener(AFTER_COMMIT)` — le notifiche email partono solo dopo il commit della transazione |
| **Builder** | `BookingDirector` assembla le entità `Booking` |
| **Optimistic Locking** | `@Version` su `Slot`, `Subscription`, `User`, `Booking` — gestione conflitti senza lock espliciti |
| **Pessimistic Locking** | `@Lock(PESSIMISTIC_WRITE)` su `SlotRepository.findByIdWithLock()` e `SubscriptionRepository.findByUserAndActiveTrueWithLock()` |
| **Fine-Grained Locking** | `ConcurrentHashMap<Long, ReentrantLock>` in `BookingServiceImpl` — difesa in profondità contro race condition a livello JVM |

---

## Domain Model

### Ruoli

| Ruolo | Descrizione |
|---|---|
| `CLIENT` | Acquista piani, prenota slot, scarica documenti, lascia recensioni |
| `PERSONAL_TRAINER` | Definisce disponibilità, gestisce fino a 50 clienti, carica schede allenamento |
| `NUTRITIONIST` | Definisce disponibilità, gestisce fino a 50 clienti, carica piani alimentari |
| `INSURANCE_MANAGER` | Gestisce le polizze infortuni legate ai piani |
| `MODERATOR` | Moderazione contenuti e supporto |
| `ADMIN` | Supervisione globale, creazione piani, gestione anagrafiche |

### Piani e Crediti

| Piano | Durata | Crediti PT/mese | Crediti Nutri/mese | Prezzo intero | Rata mensile |
|---|---|---|---|---|---|
| Basic Pack | Semestrale | 1 | 1 | € 960 | € 160 |
| Basic Pack | Annuale | 1 | 1 | € 1.800 | € 150 |
| Premium Pack | Semestrale | 2 | 2 | € 1.620 | € 270 |
| Premium Pack | Annuale | 2 | 2 | € 3.000 | € 250 |

I crediti si azzerano mensilmente (non sono cumulabili). Lo `SubscriptionScheduler` gira ogni notte a mezzanotte per il rinnovo crediti e la gestione delle rate.

### Prenotazioni

- Slot da 30 minuti generati da `WeeklySchedule` settimanali dei professionisti
- Locking a doppio livello (JVM + DB) per prevenire overbooking concorrente
- Cancellazione gratuita (credito rimborsato) se richiesta con almeno 24 ore di anticipo
- Notifiche email transazionali post-commit via `@TransactionalEventListener`

### Recensioni

Un cliente può recensire un professionista solo se:
- esiste almeno una prenotazione confermata tra la coppia **oppure** il cliente è attualmente assegnato al professionista
- non ha ancora lasciato una recensione per quella coppia (unicità garantita)

### Chat

Messaggistica real-time via STOMP/WebSocket con fallback REST per lo storico.

### Documenti

File system storage con metadati in DB. Tipologie: schede allenamento (PT), piani alimentari (Nutrizionista), polizze (Insurance Manager).

---

## Quick Start

### Prerequisiti

- **Java 21** — [Adoptium Temurin](https://adoptium.net/temurin/releases/?version=21)
- **Docker Desktop** — richiesto per il profilo `dev`

> Maven è incluso nel wrapper (`mvnw`), non serve installarlo.

### Profilo dev (database locale)

```bash
git clone <url-repository>
cd Progetto/tesi

# macOS / Linux
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Windows PowerShell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Docker Compose avvia automaticamente PostgreSQL su `localhost:5432` e pgAdmin su `localhost:5050`.  
Il database viene popolato con dati di test all'avvio.

### Profilo prod (database cloud)

```bash
# Richiede le variabili d'ambiente configurate (vedi sezione Configurazione)
./mvnw spring-boot:run
```

### Comandi utili

```bash
# Eseguire i test
./mvnw test

# Build JAR (senza test)
./mvnw clean package -DskipTests

# Reset database di sviluppo (ripopola i dati di test)
curl http://localhost:8080/api/bookings/reset-database
```

---

## Credenziali di Test

Disponibili solo con il profilo `dev`. Password comune: `password`.

| Email | Ruolo | Note |
|---|---|---|
| `pt1@test.com` | Personal Trainer | Disponibile lun/mer/ven |
| `pt2@test.com` | Personal Trainer | Disponibile mar/gio/sab |
| `nutri1@test.com` | Nutrizionista | Disponibile lun/mer/ven |
| `nutri2@test.com` | Nutrizionista | Disponibile mar/gio/sab |
| `luca@test.com` | Cliente | Assegnato a pt1 + nutri1, Basic Pack Semestrale |
| `sofia@test.com` | Cliente | Assegnato a pt1 + nutri2, Basic Pack Annuale |
| `matteo@test.com` | Cliente | Assegnato a pt2 + nutri1, Premium Pack Semestrale |
| `chiara@test.com` | Cliente | Assegnato a pt2 + nutri2, Premium Pack Annuale |
| `testreview@test.com` | Cliente | Utente dedicato al test delle recensioni |
| `admin@test.com` | Admin | Accesso completo |
| `insurance@test.com` | Insurance Manager | Gestione polizze |
| `moderator1@test.com` | Moderatore | Moderazione contenuti |

---

## API Docs

Swagger UI disponibile a runtime:

```
http://localhost:8080/swagger-ui.html
```

Endpoint principali:

| Gruppo | Base path |
|---|---|
| Autenticazione | `/api/auth` |
| Prenotazioni | `/api/bookings` |
| Slot | `/api/slots` |
| Recensioni | `/api/reviews` |
| Abbonamenti | `/api/subscriptions` |
| Profilo utente | `/api/users` |
| Documenti | `/api/documents` |
| Chat | `/api/chat` (REST) + WebSocket `/ws` |

---

## Configurazione

Variabili d'ambiente richieste in produzione:

| Variabile | Descrizione |
|---|---|
| `JWT_SECRET` | Chiave segreta per la firma dei token JWT (min. 32 caratteri) |
| `MAIL_FROM` | Indirizzo mittente delle email transazionali |
| `SPRING_MAIL_HOST` | SMTP host (es. `smtp.gmail.com`) |
| `SPRING_MAIL_PORT` | SMTP port (es. `587`) |
| `SPRING_MAIL_USERNAME` | Credenziale SMTP — username |
| `SPRING_MAIL_PASSWORD` | Credenziale SMTP — password o app password |
| `SPRING_DATASOURCE_URL` | JDBC URL del database di produzione |
| `SPRING_DATASOURCE_USERNAME` | Username database |
| `SPRING_DATASOURCE_PASSWORD` | Password database |

In sviluppo (`dev`), i valori di default sono definiti in `application-dev.properties`.

### Log4j2

Il logging è gestito da Log4j2 (`src/main/resources/log4j2-spring.xml`):

- `com.project.tesi.controller` — INFO
- `com.project.tesi.service` — DEBUG
- `com.project.tesi.security` — INFO
- Hibernate / Spring Framework — WARN
- File rolling giornaliero in `logs/app.log` (max 10 MB, 30 file)

---

## Testing

```bash
# Suite completa (260 test)
./mvnw test

# Singola classe
./mvnw test -Dtest=BookingServiceImplTest

# Report coverage JaCoCo (generato in target/site/jacoco/)
./mvnw verify
```

I test usano H2 in-memory con profilo `test` (`create-drop`). Gli scheduler sono disabilitati automaticamente durante i test.

Pattern adottati:
- `@ExtendWith(MockitoExtension.class)` + `@Mock` / `@InjectMocks` per unit test puri
- `@WebMvcTest` + `MockMvc` per i controller
- `@DisplayName` su ogni metodo per output leggibile

```
[INFO] Tests run: 260, Failures: 0, Errors: 0, Skipped: 0
```
