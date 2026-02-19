Titolo del Progetto: Piattaforma SaaS Integrata per il Wellness e la Tutela Assicurativa

1. Visione e Obiettivo del Progetto

Il progetto mira a realizzare un sistema gestionale backend (SaaS) per l'erogazione di servizi integrati di benessere e salute. L'idea innovativa risiede nell'offrire all'utente finale un percorso completo che unisce tre figure professionali in un unico abbonamento:


Personal Trainer (per l'allenamento fisico).

Nutrizionista (per il piano alimentare).

Partner Assicurativo (per la tutela dagli infortuni durante l'attività).

A differenza dei classici marketplace di prenotazione, il sistema si basa su una relazione continuativa ed esclusiva tra cliente e professionista, regolata da abbonamenti a lungo termine e un sistema di crediti mensili.


2. Architettura del Sistema

Il sistema è sviluppato seguendo un'architettura RESTful Monolitica Modulare basata su Java 21 e Spring Boot. La persistenza dei dati è affidata a PostgreSQL (containerizzato via Docker), scelto per la sua robustezza nella gestione delle transazioni (ACID) e della concorrenza.


Stack Tecnologico

Backend Framework: Spring Boot (Web, Data JPA, Security, Validation).

Database: PostgreSQL (con dialetto Hibernate ottimizzato).

Sicurezza: Spring Security + JWT (JSON Web Token) per autenticazione stateless.

Containerizzazione: Docker & Docker Compose.

Testing: JUnit 5, Mockito, H2 Database (In-Memory).

Utility: Lombok (boilerplate reduction), Swagger/OpenAPI (documentazione API).

3. Modulo Core: Gestione Abbonamenti e Crediti

Il modello di business non si basa sul pagamento della singola prestazione, ma sull'acquisto di Piani (Plans).


Tipologia Piani: I pacchetti sono vincolati a una durata temporale, specificamente Semestrale (6 mesi) o Annuale (12 mesi).

Modalità di Pagamento: L'utente può scegliere tra:

Unica Soluzione: Pagamento anticipato dell'intero importo.

Rateale: Pagamento mensile dilazionato.

Economia a Crediti: Ogni piano eroga mensilmente un plafond di crediti distinti (es. 4 crediti PT, 1 credito Nutrizionista).

I crediti si resettano ogni mese (non sono cumulabili).

Un job automatico (Scheduler) gestisce il rinnovo dei crediti e la verifica dei pagamenti rateali ogni notte.

4. Modulo Prenotazioni (Booking System)

Il cuore operativo è il calendario appuntamenti, progettato per gestire slot temporali fissi da 30 minuti.


Disponibilità Ricorrente: I professionisti non inseriscono i singoli slot manualmente. Definiscono delle regole settimanali (es. "Lunedì disponibile dalle 09:00 alle 13:00").

Generazione Automatica: Il sistema traduce queste regole in slot prenotabili reali, applicando un vincolo di preavviso di 1 settimana (le modifiche agli orari non impattano la settimana corrente per tutelare i clienti).

Gestione della Concorrenza: Per evitare l'overbooking (due utenti che prenotano lo stesso istante), il database utilizza l'Optimistic Locking (campo @Version). Se due richieste arrivano simultaneamente, solo la prima viene processata, l'altra riceve un errore gestito.

5. Regole di Business e Vincoli

A. Assegnazione Esclusiva (Il vincolo dei 50)

Per garantire la qualità del servizio, il sistema impone che ogni Cliente sia assegnato a uno specifico Personal Trainer e a uno specifico Nutrizionista.


Regola: Ogni professionista può seguire contemporaneamente un massimo di 10 Clienti.

Il sistema blocca tentativi di assegnazione che violerebbero questo limite.

B. Sistema di Recensioni Verificate ("Verified Reviews")

Il sistema di feedback è progettato per essere immune da spam o recensioni fake.


Vincolo di Assegnazione: Un cliente può recensire solo il Personal Trainer o il Nutrizionista a cui è attualmente assegnato. Non è possibile recensire professionisti con cui non si ha un rapporto di tutoraggio attivo.

Unicità: È permessa una sola recensione per relazione professionale.

6. Gestione Documentale

La piattaforma gestisce lo scambio sicuro di file tra professionisti e clienti.


Tipologie: Piani di allenamento (PT), Diete (Nutrizionista), Polizze Infortuni (Assicurazione).

Storage: I file non vengono salvati come BLOB nel database (per efficienza), ma su file system/cloud storage. Il database memorizza solo i metadati e i percorsi di accesso.

7. Attori del Sistema (Ruoli)

Il sistema gestisce 5 tipologie di utenza tramite un singolo entry-point di autenticazione ma con permessi diversificati:


CLIENT: Acquista piani, prenota slot, scarica documenti, lascia recensioni.

PERSONAL_TRAINER: Imposta disponibilità oraria, vede i suoi 10 clienti, carica schede allenamento.

NUTRITIONIST: Imposta disponibilità oraria, vede i suoi 10 clienti, carica diete.

INSURANCE_MANAGER: Gestisce la parte contrattuale delle polizze incluse nei piani.

ADMIN: Supervisione globale, creazione dei Piani, gestione anagrafiche.


questo qui sono le specifiche per il mio progetto di laurea. A queste specifiche devi aggiungere anche le seguenti informazioni:


i piani sono 2, ed entrambi possono essere semestrali o annuali, con pagamento che può avvenire in un unica soluzione o a rate.

I piani sono:

- Basic Pack: 1 credito per prenotare una call con Nutrizionista al mese e 1 credito per una call con il PT al mese

- Premium Pack: 2 credito per prenotare una call con Nutrizionista al mese e 2 credito per una call con il PT al mese


l'utente alla registrazione deve scegliere il suo nutrizionista e il suo personal trainer che saranno messi in ordine secondo un ranking dato dalla media delle recensioni ricevute e inoltre verrà mostrato il numero di utenti che attualmente i professionisti stanno seguendo (i professionisti possono seguire al massimo 50 utenti con contratto attivo)


le disponibilità dei professionisti, gli slot di tempo liberi devono essere scelti settimanalmente e gli utenti potranno collegarsi negli slot di mezz'ora disponibili 