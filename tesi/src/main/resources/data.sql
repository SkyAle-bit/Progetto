-- ============================================================
-- Kore — dati di seed per lo sviluppo locale
-- Eseguito da Spring Boot dopo la creazione dello schema
-- Hibernate (spring.sql.init.mode: always, dev profile).
--
-- Password comune per tutti gli utenti: "password"
-- BCrypt hash generato con Spring BCryptPasswordEncoder(10): $2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu
-- ============================================================

-- Piani
INSERT INTO plans (name, duration, monthly_creditspt, monthly_credits_nutri, full_price, monthly_installment_price, insurance_coverage_details)
VALUES
    ('Basic Pack Semestrale',   'SEMESTRALE', 1, 1,  960.0,  160.0, 'Copertura inclusa'),
    ('Basic Pack Annuale',      'ANNUALE',    1, 1, 1800.0,  150.0, 'Copertura inclusa'),
    ('Premium Pack Semestrale', 'SEMESTRALE', 2, 2, 1620.0,  270.0, 'Copertura inclusa'),
    ('Premium Pack Annuale',    'ANNUALE',    2, 2, 3000.0,  250.0, 'Copertura inclusa');

-- Professionisti e staff (nessuna FK verso altri utenti)
INSERT INTO users (version, email, password, first_name, last_name, role, professional_bio, assigned_pt_id, assigned_nutritionist_id)
VALUES
    (0, 'pt1@test.com',        '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Marco',   'Rossi',         'PERSONAL_TRAINER', 'Specializzato in allenamento funzionale e riabilitazione.',       NULL, NULL),
    (0, 'pt2@test.com',        '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Giulia',  'Bianchi',       'PERSONAL_TRAINER', 'Esperta in powerlifting e preparazione atletica.',                NULL, NULL),
    (0, 'nutri1@test.com',     '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Laura',   'Verdi',         'NUTRITIONIST',     'Biologa nutrizionista, esperta in dieta mediterranea e sportiva.', NULL, NULL),
    (0, 'nutri2@test.com',     '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Andrea',  'Esposito',      'NUTRITIONIST',     'Specializzato in nutrizione clinica e intolleranze alimentari.',   NULL, NULL),
    (0, 'admin@test.com',      '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Admin',   'Sistema',       'ADMIN',            NULL,                                                              NULL, NULL),
    (0, 'insurance@test.com',  '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Paolo',   'Assicurazioni', 'INSURANCE_MANAGER',NULL,                                                              NULL, NULL),
    (0, 'moderator1@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Marta',   'Moderatrice',   'MODERATOR',        NULL,                                                              NULL, NULL),
    (0, 'moderator2@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Lorenzo', 'Support',       'MODERATOR',        NULL,                                                              NULL, NULL),
    (0, 'moderator3@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Elisa',   'Care',          'MODERATOR',        NULL,                                                              NULL, NULL);

-- Clienti (referenziano PT e nutrizionista già inseriti)
INSERT INTO users (version, email, password, first_name, last_name, role, professional_bio, assigned_pt_id, assigned_nutritionist_id)
VALUES (0, 'luca@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Luca',  'Ferri',    'CLIENT', NULL,
        (SELECT id FROM users WHERE email = 'pt1@test.com'),
        (SELECT id FROM users WHERE email = 'nutri1@test.com'));

INSERT INTO users (version, email, password, first_name, last_name, role, professional_bio, assigned_pt_id, assigned_nutritionist_id)
VALUES (0, 'sofia@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Sofia', 'Conti',    'CLIENT', NULL,
        (SELECT id FROM users WHERE email = 'pt1@test.com'),
        (SELECT id FROM users WHERE email = 'nutri2@test.com'));

INSERT INTO users (version, email, password, first_name, last_name, role, professional_bio, assigned_pt_id, assigned_nutritionist_id)
VALUES (0, 'matteo@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Matteo','Galli',    'CLIENT', NULL,
        (SELECT id FROM users WHERE email = 'pt2@test.com'),
        (SELECT id FROM users WHERE email = 'nutri1@test.com'));

INSERT INTO users (version, email, password, first_name, last_name, role, professional_bio, assigned_pt_id, assigned_nutritionist_id)
VALUES (0, 'chiara@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Chiara','Fontana',  'CLIENT', NULL,
        (SELECT id FROM users WHERE email = 'pt2@test.com'),
        (SELECT id FROM users WHERE email = 'nutri2@test.com'));

INSERT INTO users (version, email, password, first_name, last_name, role, professional_bio, assigned_pt_id, assigned_nutritionist_id)
VALUES (0, 'testreview@test.com', '$2a$10$0VtW52huEimaZO64NAgNpO8NXKTrMutT24RHz..em0HI8QkxW0.eu', 'Test','Recensore','CLIENT', NULL,
        (SELECT id FROM users WHERE email = 'pt1@test.com'),
        (SELECT id FROM users WHERE email = 'nutri1@test.com'));

-- testreview: simula utente creato 40 giorni fa (abilita la logica di recensione)
UPDATE users SET created_at = NOW() - INTERVAL '40 days' WHERE email = 'testreview@test.com';

-- Abbonamenti
INSERT INTO subscriptions (version, user_id, plan_id, payment_frequency, start_date, end_date, active, current_creditspt, current_credits_nutri, last_renewal_date, installments_paid, total_installments, next_payment_date)
VALUES (0,
    (SELECT id FROM users WHERE email = 'luca@test.com'),
    (SELECT id FROM plans WHERE name = 'Basic Pack Semestrale'),
    'UNICA_SOLUZIONE', CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', true, 1, 1, CURRENT_DATE, 1, 1, NULL);

INSERT INTO subscriptions (version, user_id, plan_id, payment_frequency, start_date, end_date, active, current_creditspt, current_credits_nutri, last_renewal_date, installments_paid, total_installments, next_payment_date)
VALUES (0,
    (SELECT id FROM users WHERE email = 'sofia@test.com'),
    (SELECT id FROM plans WHERE name = 'Basic Pack Annuale'),
    'RATE_MENSILI', CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months', true, 1, 1, CURRENT_DATE, 1, 12, CURRENT_DATE + INTERVAL '1 month');

INSERT INTO subscriptions (version, user_id, plan_id, payment_frequency, start_date, end_date, active, current_creditspt, current_credits_nutri, last_renewal_date, installments_paid, total_installments, next_payment_date)
VALUES (0,
    (SELECT id FROM users WHERE email = 'matteo@test.com'),
    (SELECT id FROM plans WHERE name = 'Premium Pack Semestrale'),
    'UNICA_SOLUZIONE', CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', true, 2, 2, CURRENT_DATE, 1, 1, NULL);

INSERT INTO subscriptions (version, user_id, plan_id, payment_frequency, start_date, end_date, active, current_creditspt, current_credits_nutri, last_renewal_date, installments_paid, total_installments, next_payment_date)
VALUES (0,
    (SELECT id FROM users WHERE email = 'chiara@test.com'),
    (SELECT id FROM plans WHERE name = 'Premium Pack Annuale'),
    'RATE_MENSILI', CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months', true, 2, 2, CURRENT_DATE, 1, 12, CURRENT_DATE + INTERVAL '1 month');

INSERT INTO subscriptions (version, user_id, plan_id, payment_frequency, start_date, end_date, active, current_creditspt, current_credits_nutri, last_renewal_date, installments_paid, total_installments, next_payment_date)
VALUES (0,
    (SELECT id FROM users WHERE email = 'testreview@test.com'),
    (SELECT id FROM plans WHERE name = 'Basic Pack Semestrale'),
    'UNICA_SOLUZIONE', CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', true, 1, 1, CURRENT_DATE, 1, 1, NULL);

-- Orari settimanali
INSERT INTO weekly_schedules (professional_id, day_of_week, start_time, end_time)
VALUES
    ((SELECT id FROM users WHERE email = 'pt1@test.com'),    'MONDAY',    '09:00:00', '13:00:00'),
    ((SELECT id FROM users WHERE email = 'pt1@test.com'),    'WEDNESDAY', '15:00:00', '19:00:00'),
    ((SELECT id FROM users WHERE email = 'pt1@test.com'),    'FRIDAY',    '09:00:00', '13:00:00'),
    ((SELECT id FROM users WHERE email = 'pt2@test.com'),    'TUESDAY',   '10:00:00', '14:00:00'),
    ((SELECT id FROM users WHERE email = 'pt2@test.com'),    'THURSDAY',  '16:00:00', '20:00:00'),
    ((SELECT id FROM users WHERE email = 'pt2@test.com'),    'SATURDAY',  '09:00:00', '12:00:00'),
    ((SELECT id FROM users WHERE email = 'nutri1@test.com'), 'MONDAY',    '14:00:00', '18:00:00'),
    ((SELECT id FROM users WHERE email = 'nutri1@test.com'), 'WEDNESDAY', '09:00:00', '13:00:00'),
    ((SELECT id FROM users WHERE email = 'nutri1@test.com'), 'FRIDAY',    '10:00:00', '14:00:00'),
    ((SELECT id FROM users WHERE email = 'nutri2@test.com'), 'TUESDAY',   '09:00:00', '13:00:00'),
    ((SELECT id FROM users WHERE email = 'nutri2@test.com'), 'THURSDAY',  '15:00:00', '19:00:00'),
    ((SELECT id FROM users WHERE email = 'nutri2@test.com'), 'SATURDAY',  '10:00:00', '13:00:00');

-- Slot futuri: genera slot da domani per 14 giorni basandosi sugli orari settimanali.
-- EXTRACT(ISODOW) restituisce 1=Lunedì … 7=Domenica, stesso ordine di java.time.DayOfWeek.
INSERT INTO slots (professional_id, start_time, end_time, booked_by_id, version)
SELECT
    ws.professional_id,
    (CURRENT_DATE + offs + ws.start_time + (n * INTERVAL '30 minutes'))::timestamp,
    (CURRENT_DATE + offs + ws.start_time + (n * INTERVAL '30 minutes') + INTERVAL '30 minutes')::timestamp,
    NULL,
    0
FROM weekly_schedules ws
CROSS JOIN generate_series(1, 14) AS offs
CROSS JOIN generate_series(0,
    (EXTRACT(EPOCH FROM (ws.end_time - ws.start_time)) / 1800)::int - 1
) AS n
WHERE EXTRACT(ISODOW FROM (CURRENT_DATE + offs))::int =
    CASE ws.day_of_week
        WHEN 'MONDAY'    THEN 1  WHEN 'TUESDAY'   THEN 2  WHEN 'WEDNESDAY' THEN 3
        WHEN 'THURSDAY'  THEN 4  WHEN 'FRIDAY'    THEN 5  WHEN 'SATURDAY'  THEN 6
        WHEN 'SUNDAY'    THEN 7
    END
ON CONFLICT DO NOTHING;

-- Slot passato per chiara con pt1 (2 giorni fa — booking COMPLETED)
INSERT INTO slots (professional_id, start_time, end_time, booked_by_id, version)
VALUES (
    (SELECT id FROM users WHERE email = 'pt1@test.com'),
    (CURRENT_DATE - 2 + TIME '10:00:00')::timestamp,
    (CURRENT_DATE - 2 + TIME '10:30:00')::timestamp,
    (SELECT id FROM users WHERE email = 'chiara@test.com'),
    0
);

-- Prenotazioni campione: 8 future (un PT + un nutrizionista per ogni cliente principale)

-- PT1 — luca
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'luca@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt1@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'luca@test.com'), (SELECT id FROM users WHERE email = 'pt1@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt1@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'luca@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_luca_pt1_01', false);

-- PT1 — sofia
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'sofia@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt1@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'sofia@test.com'), (SELECT id FROM users WHERE email = 'pt1@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt1@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'sofia@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_sofia_pt1_01', false);

-- PT2 — matteo
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'matteo@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt2@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'matteo@test.com'), (SELECT id FROM users WHERE email = 'pt2@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt2@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'matteo@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_matteo_pt2_01', false);

-- PT2 — chiara
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'chiara@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt2@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'chiara@test.com'), (SELECT id FROM users WHERE email = 'pt2@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt2@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'chiara@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_chiara_pt2_01', false);

-- NUT1 — luca
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'luca@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri1@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'luca@test.com'), (SELECT id FROM users WHERE email = 'nutri1@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri1@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'luca@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_luca_nutri1_01', false);

-- NUT1 — matteo
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'matteo@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri1@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'matteo@test.com'), (SELECT id FROM users WHERE email = 'nutri1@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri1@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'matteo@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_matteo_nutri1_01', false);

-- NUT2 — sofia
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'sofia@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri2@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'sofia@test.com'), (SELECT id FROM users WHERE email = 'nutri2@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri2@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'sofia@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_sofia_nutri2_01', false);

-- NUT2 — chiara
UPDATE slots SET booked_by_id = (SELECT id FROM users WHERE email = 'chiara@test.com'), version = 1
WHERE id = (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri2@test.com') AND booked_by_id IS NULL ORDER BY start_time LIMIT 1);
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0, (SELECT id FROM users WHERE email = 'chiara@test.com'), (SELECT id FROM users WHERE email = 'nutri2@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'nutri2@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'chiara@test.com') ORDER BY start_time LIMIT 1),
    'CONFIRMED', 'https://meet.jit.si/Kore_chiara_nutri2_01', false);

-- Booking passato COMPLETED: chiara con pt1
INSERT INTO bookings (version, user_id, professional_id, slot_id, status, meeting_link, reminder_sent)
VALUES (0,
    (SELECT id FROM users WHERE email = 'chiara@test.com'),
    (SELECT id FROM users WHERE email = 'pt1@test.com'),
    (SELECT id FROM slots WHERE professional_id = (SELECT id FROM users WHERE email = 'pt1@test.com') AND booked_by_id = (SELECT id FROM users WHERE email = 'chiara@test.com') AND start_time < NOW() ORDER BY start_time LIMIT 1),
    'COMPLETED', 'https://meet.jit.si/Kore_chiara_pt1_past', true);

-- Aggiorna i crediti dopo le prenotazioni (1 PT + 1 Nutri per cliente)
UPDATE subscriptions SET current_creditspt = current_creditspt - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'luca@test.com')   AND current_creditspt > 0;
UPDATE subscriptions SET current_creditspt = current_creditspt - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'sofia@test.com')  AND current_creditspt > 0;
UPDATE subscriptions SET current_creditspt = current_creditspt - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'matteo@test.com') AND current_creditspt > 0;
UPDATE subscriptions SET current_creditspt = current_creditspt - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'chiara@test.com') AND current_creditspt > 0;

UPDATE subscriptions SET current_credits_nutri = current_credits_nutri - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'luca@test.com')   AND current_credits_nutri > 0;
UPDATE subscriptions SET current_credits_nutri = current_credits_nutri - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'sofia@test.com')  AND current_credits_nutri > 0;
UPDATE subscriptions SET current_credits_nutri = current_credits_nutri - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'matteo@test.com') AND current_credits_nutri > 0;
UPDATE subscriptions SET current_credits_nutri = current_credits_nutri - 1
WHERE user_id = (SELECT id FROM users WHERE email = 'chiara@test.com') AND current_credits_nutri > 0;
